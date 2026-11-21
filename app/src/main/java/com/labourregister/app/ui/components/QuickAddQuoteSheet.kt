package com.labourregister.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CurrencyRupee
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labourregister.app.data.ShopEntity
import com.labourregister.app.ui.theme.RegisterBackground
import com.labourregister.app.ui.theme.RegisterBorder
import com.labourregister.app.ui.theme.RegisterError
import com.labourregister.app.ui.theme.RegisterNavy
import com.labourregister.app.ui.theme.RegisterSuccess
import com.labourregister.app.ui.theme.RegisterSuccessLight
import com.labourregister.app.ui.theme.RegisterSurface
import com.labourregister.app.ui.theme.RegisterTeal
import com.labourregister.app.ui.theme.RegisterTealLight
import com.labourregister.app.ui.theme.RegisterTextPrimary
import com.labourregister.app.ui.theme.RegisterTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuickAddQuoteSheet(
    initialShop: ShopEntity? = null,
    sheetState: SheetState,
    shopNameSuggestions: List<String>,
    productNameSuggestions: List<String>,
    onFindShop: suspend (String) -> ShopEntity?,
    onSaveInquiry: (shopName: String, mobile: String, contact: String, product: String, brand: String, quantity: Int, price: String, gst: Boolean, notes: String) -> Boolean,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var shopName by remember(initialShop?.id) { mutableStateOf(initialShop?.shopName ?: "") }
    var mobileNumber by remember(initialShop?.id) { mutableStateOf(initialShop?.mobileNumber ?: "") }
    var contactName by remember(initialShop?.id) { mutableStateOf(initialShop?.contactName ?: "") }

    var productName by remember { mutableStateOf("") }
    var brandOrModel by remember { mutableStateOf("") }
    var quantityStr by remember { mutableStateOf("1") }
    var priceStr by remember { mutableStateOf("") }
    var gstIncluded by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successFlash by remember { mutableStateOf<String?>(null) }

    val productFocusRequester = remember { FocusRequester() }

    LaunchedEffect(successFlash) {
        if (successFlash != null) {
            delay(2200)
            successFlash = null
        }
    }

    // Filter shop suggestions
    val filteredShopSuggestions = remember(shopName, shopNameSuggestions) {
        val trimmed = shopName.trim()
        if (trimmed.isEmpty()) {
            shopNameSuggestions.take(5)
        } else {
            shopNameSuggestions
                .filter { it.contains(trimmed, ignoreCase = true) && !it.equals(trimmed, ignoreCase = true) }
                .take(5)
        }
    }

    // Filter product suggestions
    val filteredProductSuggestions = remember(productName, productNameSuggestions) {
        val trimmed = productName.trim()
        if (trimmed.isEmpty()) {
            productNameSuggestions.take(5)
        } else {
            productNameSuggestions
                .filter { it.contains(trimmed, ignoreCase = true) && !it.equals(trimmed, ignoreCase = true) }
                .take(5)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = RegisterSurface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (initialShop != null) "Add Quote: ${initialShop.displayTitle}" else "Quick Quote Entry",
                        style = MaterialTheme.typography.titleLarge,
                        color = RegisterTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Enter shop and product quotes in a single rapid flow",
                        style = MaterialTheme.typography.bodySmall,
                        color = RegisterTextSecondary
                    )
                }
            }

            // Quick Flash Toast
            if (successFlash != null) {
                Surface(
                    color = RegisterSuccessLight,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = RegisterSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = successFlash!!,
                            color = RegisterSuccess,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = RegisterError,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- SECTION 1: SHOP DETAILS ---
            Text(
                text = "Shop / Vendor",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = RegisterNavy
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Shop Name
            OutlinedTextField(
                value = shopName,
                onValueChange = {
                    shopName = it
                    if (errorMessage != null) errorMessage = null
                },
                label = { Text("Shop Name *") },
                placeholder = { Text("e.g. Royal Hardware, Shree Ram Elec.") },
                leadingIcon = {
                    Icon(Icons.Outlined.Storefront, contentDescription = null, tint = RegisterNavy)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RegisterNavy,
                    unfocusedBorderColor = RegisterBorder
                )
            )

            // Shop Suggestions Chips
            if (filteredShopSuggestions.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    filteredShopSuggestions.forEach { suggestion ->
                        SuggestionChip(
                            onClick = {
                                shopName = suggestion
                                coroutineScope.launch {
                                    val shop = onFindShop(suggestion)
                                    if (shop != null) {
                                        if (shop.mobileNumber.isNotBlank()) mobileNumber = shop.mobileNumber
                                        if (shop.contactName.isNotBlank()) contactName = shop.contactName
                                    }
                                }
                                if (errorMessage != null) errorMessage = null
                            },
                            label = { Text(suggestion, fontSize = 12.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = RegisterBackground,
                                labelColor = RegisterNavy
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = RegisterBorder
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Optional Mobile & Contact Person Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { value ->
                        val trimmed = value.trim()
                        val sanitized = if (trimmed.startsWith("+")) {
                            "+" + trimmed.substring(1).filter { it.isDigit() }
                        } else {
                            value.filter { it.isDigit() }
                        }
                        mobileNumber = sanitized
                    },
                    label = { Text("Mobile (opt.)") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Phone, contentDescription = null, tint = RegisterTeal, modifier = Modifier.size(16.dp))
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RegisterNavy,
                        unfocusedBorderColor = RegisterBorder
                    )
                )

                OutlinedTextField(
                    value = contactName,
                    onValueChange = { contactName = it },
                    label = { Text("Contact (opt.)") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Person, contentDescription = null, tint = RegisterTeal, modifier = Modifier.size(16.dp))
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RegisterNavy,
                        unfocusedBorderColor = RegisterBorder
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = RegisterBorder.copy(alpha = 0.5f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // --- SECTION 2: PRODUCT & PRICE ---
            Text(
                text = "Product Quote",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = RegisterNavy
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Product Name
            OutlinedTextField(
                value = productName,
                onValueChange = {
                    productName = it
                    if (errorMessage != null) errorMessage = null
                },
                label = { Text("Product Name *") },
                placeholder = { Text("e.g. Cat6 Cable 305m, 4CH DVR") },
                leadingIcon = {
                    Icon(Icons.Outlined.Inventory2, contentDescription = null, tint = RegisterNavy)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(productFocusRequester),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RegisterNavy,
                    unfocusedBorderColor = RegisterBorder
                )
            )

            // Product Suggestions Chips
            if (filteredProductSuggestions.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    filteredProductSuggestions.forEach { suggestion ->
                        SuggestionChip(
                            onClick = {
                                productName = suggestion
                                if (errorMessage != null) errorMessage = null
                            },
                            label = { Text(suggestion, fontSize = 12.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = RegisterBackground,
                                labelColor = RegisterNavy
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = RegisterBorder
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Brand / Model
            OutlinedTextField(
                value = brandOrModel,
                onValueChange = { brandOrModel = it },
                label = { Text("Brand / Model (optional)") },
                placeholder = { Text("e.g. D-Link, CP Plus, Finolex") },
                leadingIcon = {
                    Icon(Icons.Outlined.Tag, contentDescription = null, tint = RegisterNavy)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RegisterNavy,
                    unfocusedBorderColor = RegisterBorder
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quantity and Unit Price Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Quantity (default 1)
                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = { value ->
                        quantityStr = value.filter { it.isDigit() }
                    },
                    label = { Text("Qty") },
                    placeholder = { Text("1") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    modifier = Modifier.width(85.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RegisterNavy,
                        unfocusedBorderColor = RegisterBorder
                    )
                )

                // Unit Price (₹)
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { value ->
                        val sanitized = value.filter { it.isDigit() || it == '.' }
                        if (sanitized.count { it == '.' } <= 1) {
                            priceStr = sanitized
                        }
                        if (errorMessage != null) errorMessage = null
                    },
                    label = { Text("Unit Rate (₹) *") },
                    prefix = { Text("₹ ") },
                    leadingIcon = {
                        Icon(Icons.Outlined.CurrencyRupee, contentDescription = null, tint = RegisterNavy)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RegisterNavy,
                        unfocusedBorderColor = RegisterBorder
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // GST Toggle Chips
            Text(
                text = "GST Status",
                style = MaterialTheme.typography.bodySmall,
                color = RegisterTextSecondary,
                fontWeight = FontWeight.Medium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip(
                    selected = !gstIncluded,
                    onClick = { gstIncluded = false },
                    label = { Text("+ 18% GST (Excl.)", fontWeight = FontWeight.Medium) },
                    leadingIcon = if (!gstIncluded) {
                        { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RegisterTealLight,
                        selectedLabelColor = RegisterTeal
                    ),
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = gstIncluded,
                    onClick = { gstIncluded = true },
                    label = { Text("✓ Incl. 18% GST", fontWeight = FontWeight.Medium) },
                    leadingIcon = if (gstIncluded) {
                        { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RegisterTealLight,
                        selectedLabelColor = RegisterTeal
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (warranty, terms, MOQ)") },
                placeholder = { Text("e.g. Cash rate, 2 yr warranty") },
                leadingIcon = {
                    Icon(Icons.Outlined.Description, contentDescription = null, tint = RegisterNavy)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RegisterNavy,
                    unfocusedBorderColor = RegisterBorder
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Primary Button: SAVE & ADD ANOTHER
            Button(
                onClick = {
                    val qty = quantityStr.toIntOrNull()?.coerceAtLeast(1) ?: 1
                    val success = onSaveInquiry(
                        shopName,
                        mobileNumber,
                        contactName,
                        productName,
                        brandOrModel,
                        qty,
                        priceStr,
                        gstIncluded,
                        notes
                    )
                    if (success) {
                        successFlash = "Saved $qty x $productName (₹$priceStr)! Ready for next item."
                        // Clear product fields only, keep shop details intact!
                        productName = ""
                        brandOrModel = ""
                        quantityStr = "1"
                        priceStr = ""
                        notes = ""
                        productFocusRequester.requestFocus()
                    } else {
                        errorMessage = "Please enter at least a Product Name or Price."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RegisterNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("+ Save & Add Another Item", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Secondary Buttons: Save & Close OR Cancel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val qty = quantityStr.toIntOrNull()?.coerceAtLeast(1) ?: 1
                        val success = onSaveInquiry(
                            shopName,
                            mobileNumber,
                            contactName,
                            productName,
                            brandOrModel,
                            qty,
                            priceStr,
                            gstIncluded,
                            notes
                        )
                        if (success) {
                            onDismiss()
                        } else {
                            if (productName.isBlank() && priceStr.isBlank()) {
                                onDismiss()
                            } else {
                                errorMessage = "Please enter Product Name or Price."
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RegisterTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save & Close")
                }

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RegisterTextSecondary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Done / Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
