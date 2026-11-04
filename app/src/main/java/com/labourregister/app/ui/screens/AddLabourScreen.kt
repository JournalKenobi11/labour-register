package com.labourregister.app.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CurrencyRupee
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labourregister.app.ui.components.LabourInputField
import com.labourregister.app.ui.theme.RegisterBackground
import com.labourregister.app.ui.theme.RegisterBorder
import com.labourregister.app.ui.theme.RegisterError
import com.labourregister.app.ui.theme.RegisterNavy
import com.labourregister.app.ui.theme.RegisterSuccess
import com.labourregister.app.ui.theme.RegisterSuccessLight
import com.labourregister.app.ui.theme.RegisterSurface
import com.labourregister.app.ui.theme.RegisterTextPrimary
import com.labourregister.app.ui.theme.RegisterTextSecondary
import com.labourregister.app.viewmodel.LabourViewModel
import kotlinx.coroutines.delay

@Composable
fun AddLabourScreen(
    viewModel: LabourViewModel,
    modifier: Modifier = Modifier
) {
    val name by viewModel.nameInput.collectAsState()
    val village by viewModel.villageInput.collectAsState()
    val dailyCharge by viewModel.dailyChargeInput.collectAsState()
    val mobileNumber by viewModel.mobileNumberInput.collectAsState()
    val formError by viewModel.formError.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Auto-dismiss success message after 3.5 seconds
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            delay(3500)
            viewModel.clearSuccessMessage()
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
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Screen Header
            Text(
                text = "New Labour Entry",
                style = MaterialTheme.typography.headlineSmall,
                color = RegisterNavy
            )
            Text(
                text = "Fill in the details below. All fields are individually optional.",
                style = MaterialTheme.typography.bodyMedium,
                color = RegisterTextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // Success Confirmation Banner
            AnimatedVisibility(
                visible = successMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (successMessage != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = RegisterSuccessLight),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = RegisterSuccess,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = successMessage ?: "",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = RegisterSuccess
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearSuccessMessage() },
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
            }

            // Validation Error Banner
            AnimatedVisibility(
                visible = formError != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (formError != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = RegisterError.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = RegisterError,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = formError ?: "",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = RegisterError
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearFormError() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = RegisterError,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = RegisterSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Field 1: Labourer Name (QWERTY text keyboard, standard spaces allowed)
                    LabourInputField(
                        label = "Labourer Name",
                        value = name,
                        onValueChange = { viewModel.onNameChange(it) },
                        placeholder = "e.g., Ramesh Kumar",
                        leadingIcon = Icons.Outlined.Person,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field 2: Village (QWERTY text keyboard, standard spaces allowed)
                    LabourInputField(
                        label = "Village",
                        value = village,
                        onValueChange = { viewModel.onVillageChange(it) },
                        placeholder = "e.g., Rampur",
                        leadingIcon = Icons.Outlined.LocationOn,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field 3: Daily Charge (Currency: INR ₹, numeric decimal keyboard)
                    LabourInputField(
                        label = "Daily Charge",
                        value = dailyCharge,
                        onValueChange = { viewModel.onDailyChargeChange(it) },
                        placeholder = "e.g., 500 or 450.50",
                        leadingIcon = Icons.Outlined.CurrencyRupee,
                        prefixText = "₹ ",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field 4: Mobile Number (Phone keypad, preserves leading zeroes & allows +91)
                    LabourInputField(
                        label = "Mobile Number",
                        value = mobileNumber,
                        onValueChange = { viewModel.onMobileNumberChange(it) },
                        placeholder = "e.g., +919876543210 or 09876543210",
                        leadingIcon = Icons.Outlined.Phone,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.saveLabourer()
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Done / Save Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.saveLabourer()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RegisterNavy)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Done",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
