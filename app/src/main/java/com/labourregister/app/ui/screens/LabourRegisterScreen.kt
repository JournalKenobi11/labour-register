package com.labourregister.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labourregister.app.export.CsvExporter
import com.labourregister.app.ui.components.EditCellDialog
import com.labourregister.app.ui.components.LabourTable
import com.labourregister.app.ui.theme.RegisterBackground
import com.labourregister.app.ui.theme.RegisterBorder
import com.labourregister.app.ui.theme.RegisterNavy
import com.labourregister.app.ui.theme.RegisterSuccess
import com.labourregister.app.ui.theme.RegisterSuccessLight
import com.labourregister.app.ui.theme.RegisterSurface
import com.labourregister.app.ui.theme.RegisterSurfaceVariant
import com.labourregister.app.ui.theme.RegisterTeal
import com.labourregister.app.ui.theme.RegisterTealLight
import com.labourregister.app.ui.theme.RegisterTextPrimary
import com.labourregister.app.ui.theme.RegisterTextSecondary
import com.labourregister.app.viewmodel.LabourViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LabourRegisterScreen(
    viewModel: LabourViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val totalCount by viewModel.totalCount.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val labourers by viewModel.labourers.collectAsState()
    val activeEditCell by viewModel.activeEditCell.collectAsState()
    val editCellError by viewModel.editCellError.collectAsState()
    val exportStatus by viewModel.exportStatus.collectAsState()

    // SAF Document Creator for CSV Export
    val csvExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        if (uri == null) {
            viewModel.setExportStatus("CSV export cancelled.")
            return@rememberLauncherForActivityResult
        }
        coroutineScope.launch {
            try {
                // Must export EVERY saved record, not just filtered search results
                val allLabourers = viewModel.getAllLabourersForExport()
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        CsvExporter.export(allLabourers, outputStream)
                    } ?: throw IllegalStateException("Could not open output stream for selected file.")
                }
                viewModel.setExportStatus("Exported ${allLabourers.size} records to CSV successfully.")
            } catch (e: Exception) {
                viewModel.setExportStatus("Export failed: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }

    // Auto-dismiss export status banner
    LaunchedEffect(exportStatus) {
        if (exportStatus != null) {
            delay(4000)
            viewModel.clearExportStatus()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RegisterBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            // Top Section: Total Count Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = RegisterSurface),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Total Labourers",
                            style = MaterialTheme.typography.bodySmall,
                            color = RegisterTextSecondary
                        )
                        Text(
                            text = "$totalCount",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = RegisterNavy
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(RegisterSurfaceVariant, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Showing: ${labourers.size}" else "All Records",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = RegisterNavy
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar: searches across Name, Village, Mobile number
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Search by name, village, or mobile...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = RegisterTextSecondary.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = RegisterTextSecondary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearSearch() }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = RegisterTextSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = RegisterSurface,
                    unfocusedContainerColor = RegisterSurface,
                    focusedBorderColor = RegisterNavy,
                    unfocusedBorderColor = RegisterBorder,
                    focusedTextColor = RegisterTextPrimary,
                    unfocusedTextColor = RegisterTextPrimary
                )
            )

            // Prominent In-Table Editing Tip Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = RegisterTealLight.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = RegisterTeal,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Double-tap any cell (Name, Village, Charge, Mobile) to edit.",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.5.sp,
                            color = RegisterTeal
                        )
                    )
                }
            }

            // Export Status Banner
            AnimatedVisibility(
                visible = exportStatus != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (exportStatus != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = RegisterSuccessLight),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = exportStatus ?: "",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = RegisterSuccess
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearExportStatus() },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = RegisterSuccess,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Compact Tabular Register: all 4 columns visible in one glance
            LabourTable(
                labourers = labourers,
                onCellDoubleTap = { labourer, field ->
                    viewModel.startEditingCell(labourer, field)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                emptyMessage = if (searchQuery.isNotEmpty()) {
                    "No labourers found matching \"$searchQuery\"."
                } else {
                    "No labourers registered yet.\nUse 'Add Labour' below to enter records."
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Export CSV Button at bottom of screen
            Button(
                onClick = {
                    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                    val defaultFileName = "labour_register_$dateStr.csv"
                    csvExportLauncher.launch(defaultFileName)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RegisterNavy)
            ) {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Export CSV",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.5.sp
                    )
                )
            }
        }

        // Double-Tap Cell Edit Dialog
        activeEditCell?.let { editCell ->
            EditCellDialog(
                activeEditCell = editCell,
                errorMessage = editCellError,
                onSave = { newValue ->
                    viewModel.saveEditedCell(newValue)
                },
                onCancel = {
                    viewModel.dismissEditingCell()
                }
            )
        }
    }
}
