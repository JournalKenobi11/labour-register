package com.labourregister.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.labourregister.app.ui.theme.RegisterBorder
import com.labourregister.app.ui.theme.RegisterNavy
import com.labourregister.app.ui.theme.RegisterSurface
import com.labourregister.app.ui.theme.RegisterTextPrimary
import com.labourregister.app.ui.theme.RegisterTextSecondary

@Composable
fun LabourInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    prefixText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = "$label (Optional)",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        color = RegisterTextSecondary.copy(alpha = 0.6f)
                    )
                }
            },
            leadingIcon = leadingIcon?.let { icon ->
                {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = RegisterNavy
                    )
                }
            },
            prefix = prefixText?.let { prefix ->
                {
                    Text(
                        text = prefix,
                        style = MaterialTheme.typography.bodyLarge,
                        color = RegisterNavy
                    )
                }
            },
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear $label",
                            tint = RegisterTextSecondary
                        )
                    }
                }
            },
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = RegisterSurface,
                unfocusedContainerColor = RegisterSurface,
                focusedBorderColor = RegisterNavy,
                unfocusedBorderColor = RegisterBorder,
                focusedLabelColor = RegisterNavy,
                unfocusedLabelColor = RegisterTextSecondary,
                focusedTextColor = RegisterTextPrimary,
                unfocusedTextColor = RegisterTextPrimary
            )
        )
    }
}
