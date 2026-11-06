package com.labourregister.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.labourregister.app.data.LabourRepository
import com.labourregister.app.data.LabourerEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

enum class LabourerField(val displayName: String) {
    NAME("Labourer Name"),
    SKILL("Skill"),
    VILLAGE("Village"),
    DAILY_CHARGE("Daily Charge (₹)"),
    MOBILE_NUMBER("Mobile Number")
}

data class ActiveEditCell(
    val recordId: Long,
    val field: LabourerField,
    val initialValue: String,
    val recordSummary: String
)

class LabourViewModel(
    private val repository: LabourRepository
) : ViewModel() {

    // --- Add Labour Form State ---
    private val _nameInput = MutableStateFlow("")
    val nameInput: StateFlow<String> = _nameInput.asStateFlow()

    private val _skillInput = MutableStateFlow("")
    val skillInput: StateFlow<String> = _skillInput.asStateFlow()

    private val _villageInput = MutableStateFlow("")
    val villageInput: StateFlow<String> = _villageInput.asStateFlow()

    private val _dailyChargeInput = MutableStateFlow("")
    val dailyChargeInput: StateFlow<String> = _dailyChargeInput.asStateFlow()

    private val _mobileNumberInput = MutableStateFlow("")
    val mobileNumberInput: StateFlow<String> = _mobileNumberInput.asStateFlow()

    private val _formError = MutableStateFlow<String?>(null)
    val formError: StateFlow<String?> = _formError.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // --- Search & Register State ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val totalCount: StateFlow<Int> = repository.getCountFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val labourers: StateFlow<List<LabourerEntity>> = _searchQuery
        .flatMapLatest { query ->
            repository.searchLabourersFlow(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Cell Editing State ---
    private val _activeEditCell = MutableStateFlow<ActiveEditCell?>(null)
    val activeEditCell: StateFlow<ActiveEditCell?> = _activeEditCell.asStateFlow()

    private val _editCellError = MutableStateFlow<String?>(null)
    val editCellError: StateFlow<String?> = _editCellError.asStateFlow()

    // --- Delete Confirmation State ---
    private val _recordToDelete = MutableStateFlow<LabourerEntity?>(null)
    val recordToDelete: StateFlow<LabourerEntity?> = _recordToDelete.asStateFlow()

    // --- Export Status Banner/Snackbar ---
    private val _exportStatus = MutableStateFlow<String?>(null)
    val exportStatus: StateFlow<String?> = _exportStatus.asStateFlow()

    // --- Form Handlers ---
    fun onNameChange(value: String) {
        _nameInput.value = value
        if (_formError.value != null) _formError.value = null
    }

    fun onSkillChange(value: String) {
        _skillInput.value = value
        if (_formError.value != null) _formError.value = null
    }

    fun onVillageChange(value: String) {
        _villageInput.value = value
        if (_formError.value != null) _formError.value = null
    }

    fun onDailyChargeChange(value: String) {
        // Filter input to allow digits and at most one decimal point
        val sanitized = value.filter { it.isDigit() || it == '.' }
        if (sanitized.count { it == '.' } <= 1) {
            _dailyChargeInput.value = sanitized
        }
        if (_formError.value != null) _formError.value = null
    }

    fun onMobileNumberChange(value: String) {
        // Allow digits and optional leading +
        val trimmed = value.trim()
        val sanitized = if (trimmed.startsWith("+")) {
            "+" + trimmed.substring(1).filter { it.isDigit() }
        } else {
            value.filter { it.isDigit() }
        }
        _mobileNumberInput.value = sanitized
        if (_formError.value != null) _formError.value = null
    }

    fun clearFormError() {
        _formError.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    fun saveLabourer() {
        val name = _nameInput.value.trim()
        val skill = _skillInput.value.trim()
        val village = _villageInput.value.trim()
        val chargeStr = _dailyChargeInput.value.trim()
        val mobile = _mobileNumberInput.value.trim()

        // Validation Rule: Rejected ONLY if all fields are empty or whitespace
        if (name.isEmpty() && skill.isEmpty() && village.isEmpty() && chargeStr.isEmpty() && mobile.isEmpty()) {
            _formError.value = "Cannot save an empty record. Please fill in at least one field."
            return
        }

        // Daily charge validation if provided
        var paise: Long? = null
        if (chargeStr.isNotEmpty()) {
            val amount = chargeStr.toDoubleOrNull()
            if (amount == null || amount < 0) {
                _formError.value = "Please enter a valid positive daily charge (₹)."
                return
            }
            try {
                paise = (BigDecimal(chargeStr).setScale(2, RoundingMode.HALF_UP) * BigDecimal(100)).longValueExact()
            } catch (e: Exception) {
                _formError.value = "Invalid charge format."
                return
            }
        }

        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val entity = LabourerEntity(
                name = if (name.isNotEmpty()) name else null,
                skill = if (skill.isNotEmpty()) skill else null,
                village = if (village.isNotEmpty()) village else null,
                dailyChargePaise = paise,
                mobileNumber = if (mobile.isNotEmpty()) mobile else null,
                createdAt = now,
                updatedAt = now
            )
            repository.insertLabourer(entity)

            // Clear all fields
            _nameInput.value = ""
            _skillInput.value = ""
            _villageInput.value = ""
            _dailyChargeInput.value = ""
            _mobileNumberInput.value = ""
            _formError.value = null

            // Show success confirmation
            val displayName = entity.name ?: "Record"
            _successMessage.value = "$displayName added to register successfully."
        }
    }

    // --- Search Handlers ---
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    // --- Double-Tap Cell Editing ---
    fun startEditingCell(labourer: LabourerEntity, field: LabourerField) {
        val initialVal = when (field) {
            LabourerField.NAME -> labourer.name ?: ""
            LabourerField.SKILL -> labourer.skill ?: ""
            LabourerField.VILLAGE -> labourer.village ?: ""
            LabourerField.DAILY_CHARGE -> labourer.dailyChargeRupeesString
            LabourerField.MOBILE_NUMBER -> labourer.mobileNumber ?: ""
        }
        val summary = labourer.name ?: labourer.skill ?: labourer.village ?: "Record #${labourer.id}"
        _editCellError.value = null
        _activeEditCell.value = ActiveEditCell(
            recordId = labourer.id,
            field = field,
            initialValue = initialVal,
            recordSummary = summary
        )
    }

    fun dismissEditingCell() {
        _activeEditCell.value = null
        _editCellError.value = null
    }

    fun saveEditedCell(newValue: String): Boolean {
        val active = _activeEditCell.value ?: return false
        val trimmed = newValue.trim()

        viewModelScope.launch {
            when (active.field) {
                LabourerField.NAME -> {
                    val value = if (trimmed.isEmpty()) null else trimmed
                    repository.updateName(active.recordId, value)
                    dismissEditingCell()
                }
                LabourerField.SKILL -> {
                    val value = if (trimmed.isEmpty()) null else trimmed
                    repository.updateSkill(active.recordId, value)
                    dismissEditingCell()
                }
                LabourerField.VILLAGE -> {
                    val value = if (trimmed.isEmpty()) null else trimmed
                    repository.updateVillage(active.recordId, value)
                    dismissEditingCell()
                }
                LabourerField.DAILY_CHARGE -> {
                    if (trimmed.isEmpty()) {
                        repository.updateDailyCharge(active.recordId, null)
                        dismissEditingCell()
                    } else {
                        val amount = trimmed.toDoubleOrNull()
                        if (amount == null || amount < 0) {
                            _editCellError.value = "Enter a valid positive number"
                            return@launch
                        }
                        try {
                            val paise = (BigDecimal(trimmed).setScale(2, RoundingMode.HALF_UP) * BigDecimal(100)).longValueExact()
                            repository.updateDailyCharge(active.recordId, paise)
                            dismissEditingCell()
                        } catch (e: Exception) {
                            _editCellError.value = "Invalid number format"
                        }
                    }
                }
                LabourerField.MOBILE_NUMBER -> {
                    val value = if (trimmed.isEmpty()) null else trimmed
                    repository.updateMobileNumber(active.recordId, value)
                    dismissEditingCell()
                }
            }
        }
        return true
    }

    // --- Deletion Flow ---
    fun requestDelete(labourer: LabourerEntity) {
        _recordToDelete.value = labourer
    }

    fun dismissDelete() {
        _recordToDelete.value = null
    }

    fun confirmDelete() {
        val labourer = _recordToDelete.value ?: return
        viewModelScope.launch {
            repository.deleteLabourer(labourer)
            _recordToDelete.value = null
            _exportStatus.value = "Record deleted."
        }
    }

    // --- CSV Export Helper ---
    suspend fun getAllLabourersForExport(): List<LabourerEntity> {
        return repository.getAllLabourers()
    }

    fun setExportStatus(message: String) {
        _exportStatus.value = message
    }

    fun clearExportStatus() {
        _exportStatus.value = null
    }
}

class LabourViewModelFactory(
    private val repository: LabourRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LabourViewModel::class.java)) {
            return LabourViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
