package com.labourregister.app.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CurrencyRupee
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labourregister.app.data.LabourerEntity
import com.labourregister.app.ui.theme.RegisterBorder
import com.labourregister.app.ui.theme.RegisterError
import com.labourregister.app.ui.theme.RegisterNavy
import com.labourregister.app.ui.theme.RegisterSurface
import com.labourregister.app.ui.theme.RegisterSurfaceVariant
import com.labourregister.app.ui.theme.RegisterTeal
import com.labourregister.app.ui.theme.RegisterTealLight
import com.labourregister.app.ui.theme.RegisterTextPrimary
import com.labourregister.app.ui.theme.RegisterTextSecondary
import com.labourregister.app.viewmodel.LabourerField

@Composable
fun LabourCardList(
    labourers: List<LabourerEntity>,
    onCellEdit: (LabourerEntity, LabourerField) -> Unit,
    onDeleteRequest: (LabourerEntity) -> Unit,
    modifier: Modifier = Modifier,
    emptyMessage: String = "No labour records found."
) {
    if (labourers.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.Inbox,
                    contentDescription = null,
                    tint = RegisterTextSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.titleMedium,
                    color = RegisterTextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = labourers,
            key = { it.id }
        ) { labourer ->
            LabourCardItem(
                labourer = labourer,
                onCellEdit = onCellEdit,
                onDelete = { onDeleteRequest(labourer) }
            )
        }
    }
}

@Composable
fun LabourCardItem(
    labourer: LabourerEntity,
    onCellEdit: (LabourerEntity, LabourerField) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, RegisterBorder.copy(alpha = 0.7f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = RegisterSurface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Name & Delete Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = { onCellEdit(labourer, LabourerField.NAME) }
                            )
                        }
                ) {
                    Text(
                        text = labourer.name ?: "Unnamed Labourer",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.5.sp,
                            lineHeight = 22.sp,
                            color = RegisterNavy
                        ),
                        softWrap = true
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(start = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Record",
                        tint = RegisterError.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Skill Badge (Full Multiline Display)
            if (!labourer.skill.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(RegisterTealLight.copy(alpha = 0.7f))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = { onCellEdit(labourer, LabourerField.SKILL) }
                            )
                        }
                        .padding(horizontal = 9.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Build,
                            contentDescription = null,
                            tint = RegisterTeal,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = labourer.skill,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.5.sp,
                                lineHeight = 16.sp,
                                color = RegisterTeal
                            ),
                            softWrap = true
                        )
                    }
                }
            }

            // Village / Location (Full Multiline Display)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = { onCellEdit(labourer, LabourerField.VILLAGE) }
                        )
                    },
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = RegisterTextSecondary,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = labourer.village ?: "No village specified",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.5.sp,
                        lineHeight = 18.sp,
                        color = if (labourer.village != null) RegisterTextPrimary else RegisterTextSecondary.copy(alpha = 0.5f)
                    ),
                    softWrap = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = RegisterBorder.copy(alpha = 0.5f), thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Footer Row: Daily Charge & Direct-Dial Mobile Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Daily Charge
                Row(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = { onCellEdit(labourer, LabourerField.DAILY_CHARGE) }
                            )
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CurrencyRupee,
                        contentDescription = null,
                        tint = RegisterNavy,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = labourer.formattedDailyCharge,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = RegisterNavy
                        )
                    )
                    Text(
                        text = " / day",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = RegisterTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }

                // Mobile Number: TAP opens phone dialer with number pre-filled; DOUBLE-TAP edits
                val hasPhone = !labourer.mobileNumber.isNullOrBlank()
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (hasPhone) RegisterTealLight.copy(alpha = 0.6f) else RegisterSurfaceVariant)
                        .border(
                            0.8.dp,
                            if (hasPhone) RegisterTeal.copy(alpha = 0.4f) else RegisterBorder.copy(alpha = 0.6f),
                            RoundedCornerShape(8.dp)
                        )
                        .pointerInput(labourer.mobileNumber) {
                            detectTapGestures(
                                onTap = {
                                    val number = labourer.mobileNumber
                                    if (!number.isNullOrBlank()) {
                                        try {
                                            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:${Uri.encode(number.trim())}")
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                            context.startActivity(dialIntent)
                                        } catch (e: Exception) {
                                            onCellEdit(labourer, LabourerField.MOBILE_NUMBER)
                                        }
                                    } else {
                                        onCellEdit(labourer, LabourerField.MOBILE_NUMBER)
                                    }
                                },
                                onDoubleTap = {
                                    onCellEdit(labourer, LabourerField.MOBILE_NUMBER)
                                }
                            )
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Phone,
                        contentDescription = "Call",
                        tint = if (hasPhone) RegisterTeal else RegisterTextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = labourer.mobileNumber ?: "Add Phone",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = if (hasPhone) RegisterTeal else RegisterTextSecondary.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    }
}
