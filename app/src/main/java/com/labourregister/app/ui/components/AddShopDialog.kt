package com.labourregister.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.labourregister.app.data.ShopEntity
import com.labourregister.app.ui.theme.RegisterBorder
import com.labourregister.app.ui.theme.RegisterError
import com.labourregister.app.ui.theme.RegisterNavy
import com.labourregister.app.ui.theme.RegisterSurface
import com.labourregister.app.ui.theme.RegisterTextPrimary
import com.labourregister.app.ui.theme.RegisterTextSecondary

@Composable
fun AddShopDialog(
    shopToEdit: ShopEntity? = null,
    onSave: (id: Long, name: String, contact: String, mobile: String, location: String, notes: String) -> Boolean,
    onDismiss: () -> Unit
) {
    var shopName by remember { mutableStateOf(shopToEdit?.shopName ?: "") }
    var location by remember { mutableStateOf(shopToEdit?.location ?: "") }
    var contactName by remember { mutableStateOf(shopToEdit?.contactName ?: "") }
    var mobileNumber by remember { mutableStateOf(shopToEdit?.mobileNumber ?: "") }
    var notes by remember { mutableStateOf(shopToEdit?.notes ?: "") }
    var errorText by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()
    val isEditing = shopToEdit != null

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = RegisterSurface,
        title = {
            Text(
                text = if (isEditing) "Edit Shop Details" else "New Shop / Vendor",
                style = MaterialTheme.typography.titleLarge,
                color = RegisterTextPrimary
            )
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

                // Shop Name
                OutlinedTextField(
                    value = shopName,
                    onValueChange = {
                        shopName = it
                        if (errorText != null) errorText = null
                    },
                    label = { Text("Shop Name *") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Storefront,
                            contentDescription = null,
                            tint = RegisterNavy
                        )
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

                Spacer(modifier = Modifier.height(10.dp))

                // Location / Area
                OutlinedTextField(
                    value = location,
                    onValueChange = {
                        location = it
                        if (errorText != null) errorText = null
                    },
                    label = { Text("Market / Area Location") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = RegisterNavy
                        )
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

                Spacer(modifier = Modifier.height(10.dp))

                // Contact Person
                OutlinedTextField(
                    value = contactName,
                    onValueChange = {
                        contactName = it
                        if (errorText != null) errorText = null
                    },
                    label = { Text("Contact Person") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Badge,
                            contentDescription = null,
                            tint = RegisterNavy
                        )
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

                Spacer(modifier = Modifier.height(10.dp))

                // Mobile Number
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
                        if (errorText != null) errorText = null
                    },
                    label = { Text("Mobile Number") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Phone,
                            contentDescription = null,
                            tint = RegisterNavy
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RegisterNavy,
                        unfocusedBorderColor = RegisterBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = {
                        notes = it
                        if (errorText != null) errorText = null
                    },
                    label = { Text("Notes (terms, landmark, etc.)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Notes,
                            contentDescription = null,
                            tint = RegisterNavy
                        )
                    },
                    minLines = 2,
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
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
                    val success = onSave(
                        shopToEdit?.id ?: 0L,
                        shopName,
                        contactName,
                        mobileNumber,
                        location,
                        notes
                    )
                    if (!success) {
                        errorText = "Please enter at least one field."
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RegisterNavy)
            ) {
                Text(if (isEditing) "Save Changes" else "Save Shop")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RegisterTextSecondary)
            ) {
                Text("Cancel")
            }
        }
    )
}
