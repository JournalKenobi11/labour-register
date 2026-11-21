package com.labourregister.app.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labourregister.app.data.ProductQuoteEntity
import com.labourregister.app.data.ShopEntity
import com.labourregister.app.data.ShopWithQuotes
import com.labourregister.app.ui.theme.RegisterBackground
import com.labourregister.app.ui.theme.RegisterBorder
import com.labourregister.app.ui.theme.RegisterError
import com.labourregister.app.ui.theme.RegisterHeaderBg
import com.labourregister.app.ui.theme.RegisterNavy
import com.labourregister.app.ui.theme.RegisterRowAlternate
import com.labourregister.app.ui.theme.RegisterSurface
import com.labourregister.app.ui.theme.RegisterTeal
import com.labourregister.app.ui.theme.RegisterTealLight
import com.labourregister.app.ui.theme.RegisterTextPrimary
import com.labourregister.app.ui.theme.RegisterTextSecondary
import com.labourregister.app.viewmodel.QuoteField

@Composable
fun ShopCard(
    shopWithQuotes: ShopWithQuotes,
    onEditShop: (ShopEntity) -> Unit,
    onDeleteShop: (ShopEntity) -> Unit,
    onAddQuote: (ShopEntity) -> Unit,
    onEditQuote: (ProductQuoteEntity, summary: String) -> Unit,
    onDeleteQuote: (ProductQuoteEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val shop = shopWithQuotes.shop
    val quotes = shopWithQuotes.quotes
    val context = LocalContext.current

    // Collapsed by default, state remembered across list scrolls/recompositions
    var isExpanded by rememberSaveable(shop.id) { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, RegisterBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = RegisterSurface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // --- Header (Clickable to shrink / expand) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Shop Title & Contact info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = shop.displayTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = RegisterNavy,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Contact & Phone
                    if (shop.contactName.isNotBlank() || shop.mobileNumber.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            if (shop.contactName.isNotBlank()) {
                                Text(
                                    text = "👤 ${shop.contactName.trim()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = RegisterTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (shop.mobileNumber.isNotBlank()) {
                                    Text(
                                        text = " • ",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = RegisterTextSecondary
                                    )
                                }
                            }

                            if (shop.mobileNumber.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = RegisterTealLight.copy(alpha = 0.6f),
                                    modifier = Modifier.clickable {
                                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${shop.mobileNumber}"))
                                        context.startActivity(dialIntent)
                                    }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Phone,
                                            contentDescription = "Call",
                                            tint = RegisterTeal,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = shop.mobileNumber,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            color = RegisterTeal,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // SKU Count & Total Quotation Pills
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = RegisterBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, RegisterBorder)
                        ) {
                            Text(
                                text = "${shopWithQuotes.skuCount} SKUs",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = RegisterNavy,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = RegisterTealLight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, RegisterTeal.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "Total: ${shopWithQuotes.formattedTotalQuotation} (incl. GST)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = RegisterTeal,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // 3-Dot Options Menu
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = RegisterTextSecondary
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Shop Details") },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = RegisterNavy)
                            },
                            onClick = {
                                menuExpanded = false
                                onEditShop(shop)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Shop", color = RegisterError) },
                            leadingIcon = {
                                Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = RegisterError)
                            },
                            onClick = {
                                menuExpanded = false
                                onDeleteShop(shop)
                            }
                        )
                    }
                }

                // Expand / Collapse Chevron
                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Shrink" else "Expand",
                        tint = RegisterNavy,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // --- Expanded Section: Full Quotation Breakdown ---
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(color = RegisterBorder, thickness = 1.dp)

                    // Quotation Summary Bar
                    Surface(
                        color = RegisterBackground,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Base: ${shopWithQuotes.formattedTotalBase}",
                                style = MaterialTheme.typography.bodySmall,
                                color = RegisterTextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "+ 18% GST: ${shopWithQuotes.formattedTotalGst}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = RegisterNavy,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Final: ${shopWithQuotes.formattedTotalQuotation}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = RegisterTeal,
                                fontSize = 12.sp
                            )
                        }
                    }

                    HorizontalDivider(color = RegisterBorder, thickness = 0.5.dp)

                    if (quotes.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp, horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No product quotes logged for this shop yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = RegisterTextSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { onAddQuote(shop) },
                                colors = ButtonDefaults.buttonColors(containerColor = RegisterNavy),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("+ Add First Quote")
                            }
                        }
                    } else {
                        // Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(RegisterHeaderBg)
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Product & Model",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = RegisterNavy,
                                modifier = Modifier.weight(1.3f)
                            )
                            Text(
                                text = "Quoted",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = RegisterNavy,
                                modifier = Modifier.weight(0.9f)
                            )
                            Text(
                                text = "Final (18% GST)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = RegisterNavy,
                                modifier = Modifier.weight(1.0f)
                            )
                            Text(
                                text = "Actions",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = RegisterNavy,
                                modifier = Modifier.width(60.dp)
                            )
                        }

                        HorizontalDivider(color = RegisterBorder, thickness = 1.dp)

                        // Table Rows
                        quotes.forEachIndexed { index, quote ->
                            val rowBg = if (index % 2 == 1) RegisterRowAlternate else RegisterSurface
                            val summary = "${quote.productName.ifEmpty { "Product" }} (${shop.displayTitle})"

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(rowBg)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Product & Brand Column
                                Column(modifier = Modifier.weight(1.3f)) {
                                    Text(
                                        text = quote.productName.ifBlank { "-" },
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = RegisterTextPrimary
                                    )
                                    if (quote.brandOrModel.isNotBlank()) {
                                        Text(
                                            text = "↳ ${quote.brandOrModel}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = RegisterTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                    if (quote.notes.isNotBlank()) {
                                        Text(
                                            text = "📝 ${quote.notes}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = RegisterTextSecondary.copy(alpha = 0.8f),
                                            fontSize = 10.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                // Quoted Price Column
                                Column(modifier = Modifier.weight(0.9f)) {
                                    Text(
                                        text = quote.formattedPrice,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = RegisterNavy
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (quote.gstIncluded) RegisterTealLight else RegisterBackground,
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Text(
                                            text = quote.gstBadgeText,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (quote.gstIncluded) RegisterTeal else RegisterTextSecondary,
                                            modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                        )
                                    }
                                }

                                // Final Landed Price (with 18% GST breakdown)
                                Column(modifier = Modifier.weight(1.0f)) {
                                    Text(
                                        text = quote.formattedFinalPrice,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = RegisterTeal
                                    )
                                    Text(
                                        text = if (quote.gstIncluded) {
                                            "(${quote.formattedGstAmount} GST inc)"
                                        } else {
                                            "(+${quote.formattedGstAmount} GST)"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 10.sp,
                                        color = RegisterTextSecondary
                                    )
                                }

                                // Action Buttons: Direct Edit ✏️ and Delete 🗑️
                                Row(
                                    modifier = Modifier.width(60.dp),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { onEditQuote(quote, summary) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Quote",
                                            tint = RegisterNavy,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { onDeleteQuote(quote) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete Quote",
                                            tint = RegisterError.copy(alpha = 0.7f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            if (index < quotes.size - 1) {
                                HorizontalDivider(color = RegisterBorder.copy(alpha = 0.4f), thickness = 0.5.dp)
                            }
                        }

                        // Bottom Add Quote Action Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(RegisterBackground)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { onAddQuote(shop) },
                                colors = ButtonDefaults.buttonColors(containerColor = RegisterNavy),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Add Product to Shop", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
