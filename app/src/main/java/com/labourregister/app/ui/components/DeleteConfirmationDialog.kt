package com.labourregister.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.labourregister.app.data.LabourerEntity
import com.labourregister.app.ui.theme.RegisterError
import com.labourregister.app.ui.theme.RegisterSurface
import com.labourregister.app.ui.theme.RegisterTextPrimary
import com.labourregister.app.ui.theme.RegisterTextSecondary

@Composable
fun DeleteConfirmationDialog(
    labourer: LabourerEntity,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = RegisterSurface,
        title = {
            Text(
                text = "Delete Labourer Record?",
                style = MaterialTheme.typography.titleLarge,
                color = RegisterTextPrimary
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                val displayName = labourer.name ?: "Unnamed Labourer"
                val village = labourer.village?.let { "Village: $it" } ?: "Village: -"
                val charge = labourer.formattedDailyCharge.let { "Daily Charge: $it" }
                val mobile = labourer.mobileNumber?.let { "Mobile: $it" } ?: "Mobile: -"

                Text(
                    text = "Are you sure you want to permanently delete this record?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = RegisterTextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "• $displayName\n• $village\n• $charge\n• $mobile",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RegisterTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RegisterError
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = RegisterError),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Delete Permanently")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel", color = RegisterTextSecondary)
            }
        }
    )
}
