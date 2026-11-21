package com.labourregister.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labourregister.app.export.CsvExporter
import com.labourregister.app.ui.components.AddShopDialog
import com.labourregister.app.ui.components.EditQuoteDialog
import com.labourregister.app.ui.components.QuickAddQuoteSheet
import com.labourregister.app.ui.components.ShopCard
import com.labourregister.app.ui.theme.RegisterBackground
import com.labourregister.app.ui.theme.RegisterBorder
import com.labourregister.app.ui.theme.RegisterError
import com.labourregister.app.ui.theme.RegisterNavy
import com.labourregister.app.ui.theme.RegisterSuccess
import com.labourregister.app.ui.theme.RegisterSuccessLight
import com.labourregister.app.ui.theme.RegisterSurface
import com.labourregister.app.ui.theme.RegisterTextPrimary
import com.labourregister.app.ui.theme.RegisterTextSecondary
import com.labourregister.app.viewmodel.ShopInquiryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopInquiryScreen(
    viewModel: ShopInquiryViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val shopsWithQuotes by viewModel.shopsWithQuotes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val productSuggestions by viewModel.productNameSuggestions.collectAsState()
    val shopSuggestions by viewModel.shopNameSuggestions.collectAsState()

    val isQuickAddOpen by viewModel.isQuickAddSheetOpen.collectAsState()
    val preselectedShopForAdd by viewModel.preselectedShopForAdd.collectAsState()

    val shopToEdit by viewModel.shopToEdit.collectAsState()
    val shopToDelete by viewModel.shopToDelete.collectAsState()

    val quoteToEdit by viewModel.quoteToEdit.collectAsState()
    val quoteToDelete by viewModel.quoteToDelete.collectAsState()

    val statusMessage by viewModel.statusMessage.collectAsState()

    val totalQuotesCount = shopsWithQuotes.sumOf { it.quotes.size }

    // Auto-dismiss status message after 3 seconds
    LaunchedEffect(statusMessage) {
        if (statusMessage != null) {
            delay(3000)
            viewModel.clearStatusMessage()
        }
    }

    // CSV Export Launcher
    val csvExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    val allShops = viewModel.getAllShopsWithQuotesForExport()
                    withContext(Dispatchers.IO) {
                        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                            CsvExporter.exportShopInquiries(allShops, outputStream)
                        }
                    }
                    viewModel.setStatusMessage("Exported quotation successfully to CSV.")
                } catch (e: Exception) {
                    viewModel.setStatusMessage("Export failed: ${e.message}")
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RegisterBackground,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openQuickAddSheet() },
                containerColor = RegisterNavy,
                contentColor = RegisterSurface,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Quick Add Quote", fontWeight = FontWeight.SemiBold) },
                shape = RoundedCornerShape(16.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // --- Screen Header: Title + Counters + Export ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Shop Inquiries",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = RegisterNavy
                    )
                    Text(
                        text = "${shopsWithQuotes.size} Shops • $totalQuotesCount Product Quotes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = RegisterTextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // CSV Export Button
                OutlinedButton(
                    onClick = {
                        val timestamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
                        csvExportLauncher.launch("Shop_Quotation_$timestamp.csv")
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RegisterNavy),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = "Export CSV",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // --- Status Message Banner ---
            AnimatedVisibility(
                visible = statusMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = RegisterSuccessLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = statusMessage ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = RegisterSuccess,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearStatusMessage() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = RegisterSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- Search Bar ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text("Search shop, contact, product, or brand...") },
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
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = RegisterSurface,
                    unfocusedContainerColor = RegisterSurface,
                    focusedBorderColor = RegisterNavy,
                    unfocusedBorderColor = RegisterBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Strictly Shop-Wise Content List ---
            if (shopsWithQuotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.Storefront,
                            contentDescription = null,
                            tint = RegisterTextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) {
                                "No shops match '$searchQuery'."
                            } else {
                                "No shop inquiries registered yet.\nTap '+ Quick Add Quote' below to log your first price quote."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = RegisterTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(shopsWithQuotes, key = { it.shop.id }) { shopWithQuotes ->
                        ShopCard(
                            shopWithQuotes = shopWithQuotes,
                            onEditShop = { shop -> viewModel.openEditShop(shop) },
                            onDeleteShop = { shop -> viewModel.requestDeleteShop(shop) },
                            onAddQuote = { shop -> viewModel.openQuickAddSheet(shop) },
                            onEditQuote = { quote, summary -> viewModel.openEditQuote(quote, summary) },
                            onDeleteQuote = { quote -> viewModel.requestDeleteQuote(quote) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp)) // FAB clearance
                    }
                }
            }
        }
    }

    // --- Quick Add Sheet ---
    if (isQuickAddOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        QuickAddQuoteSheet(
            initialShop = preselectedShopForAdd,
            sheetState = sheetState,
            shopNameSuggestions = shopSuggestions,
            productNameSuggestions = productSuggestions,
            onFindShop = { name -> viewModel.findShopByName(name) },
            onSaveInquiry = { shop, mobile, contact, product, brand, qty, price, gst, notes ->
                viewModel.saveQuickInquiry(shop, mobile, contact, product, brand, qty, price, gst, notes)
            },
            onDismiss = { viewModel.dismissQuickAddSheet() }
        )
    }

    // --- Edit Quote Dialog (Works reliably for any field like "some into dome") ---
    if (quoteToEdit != null) {
        val (quote, summary) = quoteToEdit!!
        EditQuoteDialog(
            quote = quote,
            summary = summary,
            onSave = { quoteId, prodName, brand, qty, priceStr, gst, notes ->
                viewModel.saveEditedQuote(quoteId, prodName, brand, qty, priceStr, gst, notes)
            },
            onDismiss = { viewModel.dismissEditQuote() }
        )
    }

    // --- Edit Shop Details Dialog ---
    if (shopToEdit != null) {
        AddShopDialog(
            shopToEdit = shopToEdit,
            onSave = { id, name, contact, mobile, loc, notes ->
                viewModel.saveEditedShop(id, name, contact, mobile, loc, notes)
            },
            onDismiss = { viewModel.dismissEditShop() }
        )
    }

    // --- Delete Shop Confirmation Dialog (Fully wired to Room delete!) ---
    if (shopToDelete != null) {
        val shop = shopToDelete!!
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteShop() },
            shape = RoundedCornerShape(16.dp),
            containerColor = RegisterSurface,
            title = {
                Text(
                    text = "Delete Shop?",
                    style = MaterialTheme.typography.titleLarge,
                    color = RegisterTextPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete '${shop.displayTitle}'? All associated product quotes will also be permanently deleted.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RegisterTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmDeleteShop() },
                    colors = ButtonDefaults.buttonColors(containerColor = RegisterError)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.dismissDeleteShop() }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- Delete Quote Confirmation Dialog ---
    if (quoteToDelete != null) {
        val quote = quoteToDelete!!
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteQuote() },
            shape = RoundedCornerShape(16.dp),
            containerColor = RegisterSurface,
            title = {
                Text(
                    text = "Delete Quote?",
                    style = MaterialTheme.typography.titleLarge,
                    color = RegisterTextPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete the quote for '${quote.productName.ifEmpty { "Item" }}'?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RegisterTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmDeleteQuote() },
                    colors = ButtonDefaults.buttonColors(containerColor = RegisterError)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.dismissDeleteQuote() }) {
                    Text("Cancel")
                }
            }
        )
    }
}
