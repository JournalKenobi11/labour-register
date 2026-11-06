package com.labourregister.app.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labourregister.app.data.LabourerEntity
import com.labourregister.app.ui.theme.RegisterBorder
import com.labourregister.app.ui.theme.RegisterHeaderBg
import com.labourregister.app.ui.theme.RegisterNavy
import com.labourregister.app.ui.theme.RegisterRowAlternate
import com.labourregister.app.ui.theme.RegisterSurface
import com.labourregister.app.ui.theme.RegisterTeal
import com.labourregister.app.ui.theme.RegisterTextPrimary
import com.labourregister.app.ui.theme.RegisterTextSecondary
import com.labourregister.app.viewmodel.LabourerField

private val ColWidthName = 125.dp
private val ColWidthSkill = 115.dp
private val ColWidthVillage = 115.dp
private val ColWidthDailyCharge = 80.dp
private val ColWidthMobile = 115.dp
val TotalTableWidth = ColWidthName + ColWidthSkill + ColWidthVillage + ColWidthDailyCharge + ColWidthMobile

@Composable
fun LabourTable(
    labourers: List<LabourerEntity>,
    onCellDoubleTap: (LabourerEntity, LabourerField) -> Unit,
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

    val horizontalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .border(1.dp, RegisterBorder, RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(horizontalScrollState)
        ) {
            Column(modifier = Modifier.width(TotalTableWidth)) {
                // Table Header Row
                TableHeaderRow()

                HorizontalDivider(color = RegisterBorder, thickness = 1.dp)

                // Vertically scrollable rows
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    itemsIndexed(
                        items = labourers,
                        key = { _, item -> item.id }
                    ) { index, labourer ->
                        val isAlternate = index % 2 == 1
                        val rowBg = if (isAlternate) RegisterRowAlternate else RegisterSurface

                        TableRowItem(
                            labourer = labourer,
                            backgroundColor = rowBg,
                            onCellDoubleTap = onCellDoubleTap
                        )

                        HorizontalDivider(color = RegisterBorder.copy(alpha = 0.6f), thickness = 0.8.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun TableHeaderRow() {
    Row(
        modifier = Modifier
            .width(TotalTableWidth)
            .background(RegisterHeaderBg)
            .height(IntrinsicSize.Min)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderCell(text = "Name", width = ColWidthName)
        VerticalColumnDivider()
        HeaderCell(text = "Skill", width = ColWidthSkill)
        VerticalColumnDivider()
        HeaderCell(text = "Village", width = ColWidthVillage)
        VerticalColumnDivider()
        HeaderCell(text = "Charge (₹)", width = ColWidthDailyCharge, textAlign = TextAlign.Center)
        VerticalColumnDivider()
        HeaderCell(text = "Mobile No.", width = ColWidthMobile)
    }
}

@Composable
private fun HeaderCell(
    text: String,
    width: Dp,
    textAlign: TextAlign = TextAlign.Start
) {
    Box(
        modifier = Modifier
            .width(width)
            .padding(horizontal = 8.dp),
        contentAlignment = when (textAlign) {
            TextAlign.Center -> Alignment.Center
            TextAlign.End -> Alignment.CenterEnd
            else -> Alignment.CenterStart
        }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp,
                color = RegisterNavy
            ),
            textAlign = textAlign,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TableRowItem(
    labourer: LabourerEntity,
    backgroundColor: Color,
    onCellDoubleTap: (LabourerEntity, LabourerField) -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .width(TotalTableWidth)
            .background(backgroundColor)
            .height(IntrinsicSize.Min)
            .defaultMinSize(minHeight = 56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Column 1: Name
        DataCell(
            text = labourer.name ?: "-",
            width = ColWidthName,
            isPlaceholder = labourer.name == null,
            maxLines = Int.MAX_VALUE,
            onDoubleTap = { onCellDoubleTap(labourer, LabourerField.NAME) }
        )

        VerticalColumnDivider()

        // Column 2: Skill
        DataCell(
            text = labourer.skill ?: "-",
            width = ColWidthSkill,
            isPlaceholder = labourer.skill == null,
            maxLines = Int.MAX_VALUE,
            onDoubleTap = { onCellDoubleTap(labourer, LabourerField.SKILL) }
        )

        VerticalColumnDivider()

        // Column 3: Village
        DataCell(
            text = labourer.village ?: "-",
            width = ColWidthVillage,
            isPlaceholder = labourer.village == null,
            maxLines = Int.MAX_VALUE,
            onDoubleTap = { onCellDoubleTap(labourer, LabourerField.VILLAGE) }
        )

        VerticalColumnDivider()

        // Column 4: Daily Charge
        DataCell(
            text = labourer.formattedDailyCharge,
            width = ColWidthDailyCharge,
            isPlaceholder = labourer.dailyChargePaise == null,
            textAlign = TextAlign.Center,
            maxLines = 2,
            onDoubleTap = { onCellDoubleTap(labourer, LabourerField.DAILY_CHARGE) }
        )

        VerticalColumnDivider()

        // Column 5: Mobile Number (TAP opens keypad dialer; DOUBLE-TAP edits)
        val hasPhone = !labourer.mobileNumber.isNullOrBlank()
        DataCell(
            text = labourer.mobileNumber ?: "-",
            width = ColWidthMobile,
            isPlaceholder = labourer.mobileNumber == null,
            textColor = if (hasPhone) RegisterTeal else null,
            maxLines = 2,
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
                        onCellDoubleTap(labourer, LabourerField.MOBILE_NUMBER)
                    }
                }
            },
            onDoubleTap = { onCellDoubleTap(labourer, LabourerField.MOBILE_NUMBER) }
        )
    }
}

@Composable
private fun VerticalColumnDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .fillMaxHeight()
            .background(RegisterBorder.copy(alpha = 0.5f))
    )
}

@Composable
private fun DataCell(
    text: String,
    width: Dp,
    isPlaceholder: Boolean,
    textAlign: TextAlign = TextAlign.Start,
    textColor: Color? = null,
    maxLines: Int = Int.MAX_VALUE,
    onTap: (() -> Unit)? = null,
    onDoubleTap: () -> Unit
) {
    val currentOnDoubleTap by rememberUpdatedState(onDoubleTap)
    val currentOnTap by rememberUpdatedState(onTap)

    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .pointerInput(text) {
                detectTapGestures(
                    onTap = {
                        currentOnTap?.invoke()
                    },
                    onDoubleTap = {
                        currentOnDoubleTap()
                    }
                )
            }
            .padding(horizontal = 8.dp, vertical = 8.dp),
        contentAlignment = when (textAlign) {
            TextAlign.Center -> Alignment.Center
            TextAlign.End -> Alignment.CenterEnd
            else -> Alignment.CenterStart
        }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 12.5.sp,
                lineHeight = 17.sp,
                color = when {
                    isPlaceholder -> RegisterTextSecondary.copy(alpha = 0.45f)
                    textColor != null -> textColor
                    else -> RegisterTextPrimary
                }
            ),
            textAlign = textAlign,
            softWrap = true,
            maxLines = maxLines,
            overflow = TextOverflow.Clip
        )
    }
}
