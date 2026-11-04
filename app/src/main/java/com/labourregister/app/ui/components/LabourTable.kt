package com.labourregister.app.ui.components

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
import com.labourregister.app.ui.theme.RegisterTextPrimary
import com.labourregister.app.ui.theme.RegisterTextSecondary
import com.labourregister.app.viewmodel.LabourerField

// Compact column widths designed to fit typical phone screens (368dp total)
// in one glance with zero or minimal scrolling, while allowing names to wrap cleanly onto 2 lines
private val ColWidthName = 106.dp
private val ColWidthVillage = 84.dp
private val ColWidthDailyCharge = 74.dp
private val ColWidthMobile = 104.dp
val TotalTableWidth = ColWidthName + ColWidthVillage + ColWidthDailyCharge + ColWidthMobile

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
        // Horizontally scrollable container (safe for smaller screens or enlarged display zoom)
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
            .padding(horizontal = 6.dp),
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
    Row(
        modifier = Modifier
            .width(TotalTableWidth)
            .background(backgroundColor)
            .height(IntrinsicSize.Min)
            .defaultMinSize(minHeight = 52.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Column 1: Name (multiline wrap: e.g. first name on line 1, surname on line 2)
        DataCell(
            text = labourer.name ?: "-",
            width = ColWidthName,
            isPlaceholder = labourer.name == null,
            maxLines = 3,
            onDoubleTap = { onCellDoubleTap(labourer, LabourerField.NAME) }
        )

        VerticalColumnDivider()

        // Column 2: Village (multiline wrap if needed)
        DataCell(
            text = labourer.village ?: "-",
            width = ColWidthVillage,
            isPlaceholder = labourer.village == null,
            maxLines = 3,
            onDoubleTap = { onCellDoubleTap(labourer, LabourerField.VILLAGE) }
        )

        VerticalColumnDivider()

        // Column 3: Daily Charge (e.g. "₹600")
        DataCell(
            text = labourer.formattedDailyCharge,
            width = ColWidthDailyCharge,
            isPlaceholder = labourer.dailyChargePaise == null,
            textAlign = TextAlign.Center,
            maxLines = 2,
            onDoubleTap = { onCellDoubleTap(labourer, LabourerField.DAILY_CHARGE) }
        )

        VerticalColumnDivider()

        // Column 4: Mobile Number
        DataCell(
            text = labourer.mobileNumber ?: "-",
            width = ColWidthMobile,
            isPlaceholder = labourer.mobileNumber == null,
            maxLines = 2,
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
    maxLines: Int = 2,
    onDoubleTap: () -> Unit
) {
    val currentOnDoubleTap by rememberUpdatedState(onDoubleTap)

    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        currentOnDoubleTap()
                    }
                )
            }
            .padding(horizontal = 6.dp, vertical = 6.dp),
        contentAlignment = when (textAlign) {
            TextAlign.Center -> Alignment.Center
            TextAlign.End -> Alignment.CenterEnd
            else -> Alignment.CenterStart
        }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                lineHeight = 16.sp,
                color = if (isPlaceholder) RegisterTextSecondary.copy(alpha = 0.45f) else RegisterTextPrimary
            ),
            textAlign = textAlign,
            softWrap = true,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}
