package com.labourregister.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.labourregister.app.ui.theme.RegisterBorder
import com.labourregister.app.ui.theme.RegisterError
import com.labourregister.app.ui.theme.RegisterNavy
import com.labourregister.app.ui.theme.RegisterSurface
import com.labourregister.app.ui.theme.RegisterTextPrimary
import com.labourregister.app.ui.theme.RegisterTextSecondary
import com.labourregister.app.viewmodel.ActiveEditCell
import com.labourregister.app.viewmodel.LabourerField

@Composable
fun EditCellDialog(
    activeEditCell: ActiveEditCell,
    errorMessage: String?,
    onSave: (String) -> Unit,
    onCancel: () -> Unit
) {
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = activeEditCell.initialValue,
                selection = TextRange(0, activeEditCell.initialValue.length)
            )
        )
    }

    val focusRequester = remember { FocusRequester() }

    val (keyboardType, capitalization, prefixText) = when (activeEditCell.field) {
        LabourerField.NAME -> Triple(KeyboardType.Text, KeyboardCapitalization.Words, null)
        LabourerField.VILLAGE -> Triple(KeyboardType.Text, KeyboardCapitalization.Words, null)
        LabourerField.DAILY_CHARGE -> Triple(KeyboardType.Decimal, KeyboardCapitalization.None, "₹ ")
        LabourerField.MOBILE_NUMBER -> Triple(KeyboardType.Phone, KeyboardCapitalization.None, null)
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onCancel,
        shape = RoundedCornerShape(16.dp),
        containerColor = RegisterSurface,
        title = {
            Column {
                Text(
                    text = "Edit ${activeEditCell.field.displayName}",
                    style = MaterialTheme.typography.titleLarge,
                    color = RegisterTextPrimary
                )
                if (activeEditCell.recordSummary.isNotEmpty()) {
                    Text(
                        text = "For: ${activeEditCell.recordSummary}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = RegisterTextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { newTfv ->
                        val text = newTfv.text
                        when (activeEditCell.field) {
                            LabourerField.DAILY_CHARGE -> {
                                val filtered = text.filter { it.isDigit() || it == '.' }
                                if (filtered.count { it == '.' } <= 1) {
                                    textFieldValue = newTfv.copy(text = filtered)
                                }
                            }
                            LabourerField.MOBILE_NUMBER -> {
                                val trimmed = text.trim()
                                val filtered = if (trimmed.startsWith("+")) {
                                    "+" + trimmed.substring(1).filter { it.isDigit() }
                                } else {
                                    text.filter { it.isDigit() }
                                }
                                textFieldValue = newTfv.copy(text = filtered)
                            }
                            else -> {
                                textFieldValue = newTfv
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    prefix = prefixText?.let { prefix ->
                        {
                            Text(
                                text = prefix,
                                style = MaterialTheme.typography.bodyLarge,
                                color = RegisterNavy
                            )
                        }
                    },
                    singleLine = true,
                    isError = errorMessage != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        capitalization = capitalization,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onSave(textFieldValue.text) }
                    ),
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

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = RegisterError
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tip: Clearing the field makes it empty.",
                    style = MaterialTheme.typography.labelSmall,
                    color = RegisterTextSecondary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(textFieldValue.text) },
                colors = ButtonDefaults.buttonColors(containerColor = RegisterNavy),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel", color = RegisterTextSecondary)
            }
        }
    )
}
