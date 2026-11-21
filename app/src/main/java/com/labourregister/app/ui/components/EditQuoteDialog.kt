package com.labourregister.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CurrencyRupee
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.labourregister.app.data.ProductQuoteEntity
import com.labourregister.app.ui.theme.RegisterBorder
import com.labourregister.app.ui.theme.RegisterError
import com.labourregister.app.ui.theme.RegisterNavy
import com.labourregister.app.ui.theme.RegisterSurface
import com.labourregister.app.ui.theme.RegisterTeal
import com.labourregister.app.ui.theme.RegisterTealLight
import com.labourregister.app.ui.theme.RegisterTextPrimary
import com.labourregister.app.ui.theme.RegisterTextSecondary

@Composable
fun EditQuoteDialog(
    quote: ProductQuoteEntity,
    summary: String,
    onSave: (quoteId: Long, productName: String, brand: String, priceStr: String, gstIncluded: Boolean, notes: String) -> Boolean,
    onDismiss: () -> Unit
) {
    // Keyed on quote.id to ensure fresh state every time dialog opens
    var productName by remember(quote.id) { mutableStateOf(quote.productName) }
    var brandOrModel by remember(quote.id) { mutableStateOf(quote.brandOrModel) }
    var priceStr by remember(quote.id) { mutableStateOf(quote.priceRupeesString) }
    var gstIncluded by remember(quote.id) { mutableStateOf(quote.gstIncluded) }
    var notes by remember(quote.id) { mutableStateOf(quote.notes) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = RegisterSurface,
        title = {
            Column {
                Text(
                    text = "Edit Product Quote",
                    style = MaterialTheme.typography.titleLarge,
                    color = RegisterTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                if (summary.isNotEmpty()) {
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = RegisterTextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                if (errorText != null) {
                    Text(
                        text = errorText!!,
                        color = RegisterError,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Product Name
                OutlinedTextField(
                    value = productName,
                    onValueChange = {
                        productName = it
                        if (errorText != null) errorText = null
                    },
                    label = { Text("Product Name *") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Inventory2, contentDescription = null, tint = RegisterNavy)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RegisterNavy,
                        unfocusedBorderColor = RegisterBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Brand / Model
                OutlinedTextField(
                    value = brandOrModel,
                    onValueChange = { brandOrModel = it },
                    label = { Text("Brand / Model") },
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

                Spacer(modifier = Modifier.height(10.dp))

                // Price (₹)
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { value ->
                        val sanitized = value.filter { it.isDigit() || it == '.' }
                        if (sanitized.count { it == '.' } <= 1) {
                            priceStr = sanitized
                        }
                        if (errorText != null) errorText = null
                    },
                    label = { Text("Price (₹)") },
                    prefix = { Text("₹ ") },
                    leadingIcon = {
                        Icon(Icons.Outlined.CurrencyRupee, contentDescription = null, tint = RegisterNavy)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RegisterNavy,
                        unfocusedBorderColor = RegisterBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !gstIncluded,
                        onClick = { gstIncluded = false },
                        label = { Text("+ 18% GST (Excl.)") },
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
                        label = { Text("✓ Incl. 18% GST") },
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

                Spacer(modifier = Modifier.height(10.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val success = onSave(quote.id, productName, brandOrModel, priceStr, gstIncluded, notes)
                    if (!success) {
                        errorText = "Please enter product name or price."
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RegisterNavy)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
