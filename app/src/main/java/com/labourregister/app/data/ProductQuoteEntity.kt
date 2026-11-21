package com.labourregister.app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Locale
import kotlin.math.roundToLong

@Entity(
    tableName = "product_quotes",
    foreignKeys = [
        ForeignKey(
            entity = ShopEntity::class,
            parentColumns = ["id"],
            childColumns = ["shopId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["shopId"])
    ]
)
data class ProductQuoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "shopId")
    val shopId: Long,

    @ColumnInfo(name = "productName")
    val productName: String = "",

    @ColumnInfo(name = "brandOrModel")
    val brandOrModel: String = "",

    @ColumnInfo(name = "pricePaise")
    val pricePaise: Long? = null,

    @ColumnInfo(name = "gstIncluded")
    val gstIncluded: Boolean = false,

    @ColumnInfo(name = "notes")
    val notes: String = "",

    @ColumnInfo(name = "createdAt")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updatedAt")
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Raw quoted price formatted, e.g. "₹5000" or "₹5000.50", or "-" if null.
     */
    val formattedPrice: String
        get() = formatPaise(pricePaise)

    val priceRupeesString: String
        get() = paiseToPlainRupees(pricePaise)

    /**
     * Standard Electronics GST (18%) Calculations:
     * - If gstIncluded == false (+18% GST):
     *     GST amount = 18% of pricePaise
     *     Final landed price = pricePaise + GST amount
     * - If gstIncluded == true (Incl. 18% GST):
     *     Final landed price = pricePaise
     *     GST amount = pricePaise * 18 / 118
     */
    val gstAmountPaise: Long?
        get() = when (pricePaise) {
            null -> null
            else -> if (gstIncluded) {
                (pricePaise * 18.0 / 118.0).roundToLong()
            } else {
                (pricePaise * 0.18).roundToLong()
            }
        }

    val finalPricePaise: Long?
        get() = when (pricePaise) {
            null -> null
            else -> if (gstIncluded) {
                pricePaise
            } else {
                pricePaise + (gstAmountPaise ?: 0L)
            }
        }

    val basePricePaise: Long?
        get() = when (pricePaise) {
            null -> null
            else -> if (gstIncluded) {
                pricePaise - (gstAmountPaise ?: 0L)
            } else {
                pricePaise
            }
        }

    val formattedGstAmount: String
        get() = formatPaise(gstAmountPaise)

    val formattedFinalPrice: String
        get() = formatPaise(finalPricePaise)

    val finalPriceRupeesString: String
        get() = paiseToPlainRupees(finalPricePaise)

    val gstAmountRupeesString: String
        get() = paiseToPlainRupees(gstAmountPaise)

    val gstBadgeText: String
        get() = if (gstIncluded) "Incl. 18% GST" else "+18% GST"

    val isValid: Boolean
        get() = productName.isNotBlank() || pricePaise != null

    companion object {
        fun formatPaise(paise: Long?): String {
            if (paise == null) return "-"
            val rupees = paise / 100.0
            return if (paise % 100L == 0L) {
                "₹${paise / 100L}"
            } else {
                String.format(Locale.US, "₹%.2f", rupees)
            }
        }

        fun paiseToPlainRupees(paise: Long?): String {
            if (paise == null) return ""
            val rupees = paise / 100.0
            return if (paise % 100L == 0L) {
                (paise / 100L).toString()
            } else {
                String.format(Locale.US, "%.2f", rupees)
            }
        }
    }
}
