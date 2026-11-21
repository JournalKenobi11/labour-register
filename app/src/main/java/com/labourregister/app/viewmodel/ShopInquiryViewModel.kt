package com.labourregister.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.labourregister.app.data.ProductQuoteEntity
import com.labourregister.app.data.QuoteWithShop
import com.labourregister.app.data.ShopEntity
import com.labourregister.app.data.ShopInquiryRepository
import com.labourregister.app.data.ShopWithQuotes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

enum class QuoteField(val displayName: String) {
    PRODUCT_NAME("Product Name"),
    BRAND_OR_MODEL("Brand / Model"),
    PRICE("Price (₹)"),
    NOTES("Notes")
}

data class ActiveEditQuoteCell(
    val quoteId: Long,
    val field: QuoteField,
    val initialValue: String,
    val initialGst: Boolean,
    val summary: String
)

class ShopInquiryViewModel(
    private val repository: ShopInquiryRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val baseShopsFlow = repository.getAllShopsWithQuotesFlow()

    // --- Strictly Shop-Wise View with Live Search Filtering ---
    val shopsWithQuotes: StateFlow<List<ShopWithQuotes>> = combine(
        baseShopsFlow,
        _searchQuery
    ) { shops, query ->
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            shops
        } else {
            shops.mapNotNull { shopWithQuotes ->
                val shop = shopWithQuotes.shop
                val shopMatches = shop.shopName.contains(trimmed, ignoreCase = true) ||
                        shop.contactName.contains(trimmed, ignoreCase = true) ||
                        shop.mobileNumber.contains(trimmed, ignoreCase = true) ||
                        shop.notes.contains(trimmed, ignoreCase = true)

                val matchingQuotes = shopWithQuotes.quotes.filter { quote ->
                    quote.productName.contains(trimmed, ignoreCase = true) ||
                            quote.brandOrModel.contains(trimmed, ignoreCase = true) ||
                            quote.notes.contains(trimmed, ignoreCase = true)
                }

                if (shopMatches) {
                    shopWithQuotes
                } else if (matchingQuotes.isNotEmpty()) {
                    shopWithQuotes.copy(quotes = matchingQuotes)
                } else {
                    null
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- Autocomplete Suggestions ---
    val productNameSuggestions: StateFlow<List<String>> = repository.getDistinctProductNames()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val shopNameSuggestions: StateFlow<List<String>> = repository.getDistinctShopNames()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Quick Add Sheet Control ---
    private val _isQuickAddSheetOpen = MutableStateFlow(false)
    val isQuickAddSheetOpen: StateFlow<Boolean> = _isQuickAddSheetOpen.asStateFlow()

    // Target shop for directly adding quotes inside an expanded shop card
    private val _preselectedShopForAdd = MutableStateFlow<ShopEntity?>(null)
    val preselectedShopForAdd: StateFlow<ShopEntity?> = _preselectedShopForAdd.asStateFlow()

    // --- Shop Deletion & Editing State ---
    private val _shopToDelete = MutableStateFlow<ShopEntity?>(null)
    val shopToDelete: StateFlow<ShopEntity?> = _shopToDelete.asStateFlow()

    private val _shopToEdit = MutableStateFlow<ShopEntity?>(null)
    val shopToEdit: StateFlow<ShopEntity?> = _shopToEdit.asStateFlow()

    // --- Quote Deletion & Editing State ---
    private val _quoteToDelete = MutableStateFlow<ProductQuoteEntity?>(null)
    val quoteToDelete: StateFlow<ProductQuoteEntity?> = _quoteToDelete.asStateFlow()

    private val _quoteToEdit = MutableStateFlow<Pair<ProductQuoteEntity, String>?>(null)
    val quoteToEdit: StateFlow<Pair<ProductQuoteEntity, String>?> = _quoteToEdit.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // --- Search Handlers ---
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    // --- Quick Add Sheet Handlers ---
    fun openQuickAddSheet(shop: ShopEntity? = null) {
        _preselectedShopForAdd.value = shop
        _isQuickAddSheetOpen.value = true
    }

    fun dismissQuickAddSheet() {
        _isQuickAddSheetOpen.value = false
        _preselectedShopForAdd.value = null
    }

    suspend fun findShopByName(name: String): ShopEntity? {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return null
        return repository.getShopByName(trimmed)
    }

    /**
     * Unified 1-Step Quick Register:
     * Saves the quote AND seamlessly links/creates the shop behind the scenes.
     */
    fun saveQuickInquiry(
        shopName: String,
        mobileNumber: String,
        contactName: String,
        productName: String,
        brandOrModel: String,
        quantity: Int = 1,
        priceStr: String,
        gstIncluded: Boolean,
        notes: String
    ): Boolean {
        val trimmedShop = shopName.trim().ifEmpty { "General Shop" }
        val trimmedMobile = mobileNumber.trim()
        val trimmedContact = contactName.trim()
        val trimmedProduct = productName.trim()
        val trimmedBrand = brandOrModel.trim()
        val trimmedPriceStr = priceStr.trim()
        val trimmedNotes = notes.trim()
        val sanitizedQty = quantity.coerceAtLeast(1)

        var paise: Long? = null
        if (trimmedPriceStr.isNotEmpty()) {
            val amount = trimmedPriceStr.toDoubleOrNull()
            if (amount == null || amount < 0) {
                return false
            }
            try {
                paise = (BigDecimal(trimmedPriceStr).setScale(2, RoundingMode.HALF_UP) * BigDecimal(100)).longValueExact()
            } catch (e: Exception) {
                return false
            }
        }

        if (trimmedProduct.isEmpty() && paise == null) {
            return false
        }

        viewModelScope.launch {
            val now = System.currentTimeMillis()

            // 1. Resolve or auto-create shop
            val existingShop = repository.getShopByName(trimmedShop)
            val shopId: Long = if (existingShop != null) {
                val updatedMobile = if (trimmedMobile.isNotEmpty()) trimmedMobile else existingShop.mobileNumber
                val updatedContact = if (trimmedContact.isNotEmpty()) trimmedContact else existingShop.contactName
                if (updatedMobile != existingShop.mobileNumber || updatedContact != existingShop.contactName) {
                    repository.updateShop(
                        existingShop.copy(
                            mobileNumber = updatedMobile,
                            contactName = updatedContact,
                            updatedAt = now
                        )
                    )
                }
                existingShop.id
            } else {
                repository.insertShop(
                    ShopEntity(
                        shopName = trimmedShop,
                        contactName = trimmedContact,
                        mobileNumber = trimmedMobile,
                        createdAt = now,
                        updatedAt = now
                    )
                )
            }

            // 2. Insert quote directly under this shop
            val quoteEntity = ProductQuoteEntity(
                shopId = shopId,
                productName = trimmedProduct,
                brandOrModel = trimmedBrand,
                quantity = sanitizedQty,
                pricePaise = paise,
                gstIncluded = gstIncluded,
                notes = trimmedNotes,
                createdAt = now,
                updatedAt = now
            )
            repository.insertQuote(quoteEntity)
            _statusMessage.value = "Saved: ${quoteEntity.productName.ifEmpty { "Item" }} (${sanitizedQty}x @ ${quoteEntity.formattedUnitFinalPrice} = ${quoteEntity.formattedLineFinalPrice})"
        }
        return true
    }

    // --- Shop Management Handlers ---
    fun openEditShop(shop: ShopEntity) {
        _shopToEdit.value = shop
    }

    fun dismissEditShop() {
        _shopToEdit.value = null
    }

    fun saveEditedShop(
        id: Long,
        shopName: String,
        contactName: String,
        mobileNumber: String,
        location: String,
        notes: String
    ): Boolean {
        val trimmedName = shopName.trim()
        if (trimmedName.isEmpty()) return false

        viewModelScope.launch {
            val existing = repository.getShopById(id) ?: return@launch
            val now = System.currentTimeMillis()
            repository.updateShop(
                existing.copy(
                    shopName = trimmedName,
                    contactName = contactName.trim(),
                    mobileNumber = mobileNumber.trim(),
                    location = location.trim(),
                    notes = notes.trim(),
                    updatedAt = now
                )
            )
            _statusMessage.value = "Shop details updated."
            dismissEditShop()
        }
        return true
    }

    fun requestDeleteShop(shop: ShopEntity) {
        _shopToDelete.value = shop
    }

    fun dismissDeleteShop() {
        _shopToDelete.value = null
    }

    fun confirmDeleteShop() {
        val shop = _shopToDelete.value ?: return
        viewModelScope.launch {
            repository.deleteShop(shop)
            _shopToDelete.value = null
            _statusMessage.value = "${shop.displayTitle} and its quotes deleted."
        }
    }

    // --- Quote Management Handlers ---
    fun openEditQuote(quote: ProductQuoteEntity, summary: String) {
        _quoteToEdit.value = Pair(quote, summary)
    }

    fun dismissEditQuote() {
        _quoteToEdit.value = null
    }

    fun saveEditedQuote(
        quoteId: Long,
        productName: String,
        brandOrModel: String,
        quantity: Int = 1,
        priceStr: String,
        gstIncluded: Boolean,
        notes: String
    ): Boolean {
        val trimmedProduct = productName.trim()
        val trimmedBrand = brandOrModel.trim()
        val trimmedPrice = priceStr.trim()
        val trimmedNotes = notes.trim()
        val sanitizedQty = quantity.coerceAtLeast(1)

        var paise: Long? = null
        if (trimmedPrice.isNotEmpty()) {
            val amount = trimmedPrice.toDoubleOrNull()
            if (amount == null || amount < 0) return false
            try {
                paise = (BigDecimal(trimmedPrice).setScale(2, RoundingMode.HALF_UP) * BigDecimal(100)).longValueExact()
            } catch (e: Exception) {
                return false
            }
        }

        if (trimmedProduct.isEmpty() && paise == null) return false

        viewModelScope.launch {
            repository.updateQuoteFull(quoteId, trimmedProduct, trimmedBrand, sanitizedQty, paise, gstIncluded, trimmedNotes)
            _statusMessage.value = "Updated $trimmedProduct."
            dismissEditQuote()
        }
        return true
    }

    fun requestDeleteQuote(quote: ProductQuoteEntity) {
        _quoteToDelete.value = quote
    }

    fun dismissDeleteQuote() {
        _quoteToDelete.value = null
    }

    fun confirmDeleteQuote() {
        val quote = _quoteToDelete.value ?: return
        viewModelScope.launch {
            repository.deleteQuote(quote)
            _quoteToDelete.value = null
            _statusMessage.value = "Quote deleted."
        }
    }

    // --- CSV Export Helper ---
    suspend fun getAllShopsWithQuotesForExport(): List<ShopWithQuotes> {
        return repository.getAllShopsWithQuotesOnce()
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun setStatusMessage(msg: String) {
        _statusMessage.value = msg
    }
}

class ShopInquiryViewModelFactory(
    private val repository: ShopInquiryRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopInquiryViewModel::class.java)) {
            return ShopInquiryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
