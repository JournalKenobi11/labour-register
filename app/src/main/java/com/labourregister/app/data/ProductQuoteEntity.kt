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

    @ColumnInfo(name = "quantity", defaultValue = "1")
    val quantity: Int = 1,

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
     * Raw unit price formatted, e.g. "₹1150" or "₹1150.50", or "-" if null.
     */
    val formattedPrice: String
        get() = formatPaise(pricePaise)

    val priceRupeesString: String
        get() = paiseToPlainRupees(pricePaise)

    /**
     * Unit 18% GST Amount in paise:
     * - If gstIncluded == false (+18% GST): 18% of pricePaise
     * - If gstIncluded == true (Incl. 18% GST): pricePaise * 18 / 118
     */
    val unitGstPaise: Long?
        get() = when (pricePaise) {
            null -> null
            else -> if (gstIncluded) {
                (pricePaise * 18.0 / 118.0).roundToLong()
            } else {
                (pricePaise * 0.18).roundToLong()
            }
        }

    /**
     * Unit Final Landed Price (with 18% GST included):
     */
    val unitFinalPricePaise: Long?
        get() = when (pricePaise) {
            null -> null
            else -> if (gstIncluded) {
                pricePaise
            } else {
                pricePaise + (unitGstPaise ?: 0L)
            }
        }

    val unitBasePricePaise: Long?
        get() = when (pricePaise) {
            null -> null
            else -> if (gstIncluded) {
                pricePaise - (unitGstPaise ?: 0L)
            } else {
                pricePaise
            }
        }

    // --- Line Totals multiplied by Quantity ---
    val lineFinalPricePaise: Long?
        get() = unitFinalPricePaise?.let { it * quantity.coerceAtLeast(1) }

    val lineGstPaise: Long?
        get() = unitGstPaise?.let { it * quantity.coerceAtLeast(1) }

    val lineBasePaise: Long?
        get() = unitBasePricePaise?.let { it * quantity.coerceAtLeast(1) }

    // --- Formatted Strings ---
    val formattedUnitFinalPrice: String
        get() = formatPaise(unitFinalPricePaise)

    val formattedUnitGst: String
        get() = formatPaise(unitGstPaise)

    val formattedLineFinalPrice: String
        get() = formatPaise(lineFinalPricePaise)

    val formattedLineGst: String
        get() = formatPaise(lineGstPaise)

    val lineFinalPriceRupeesString: String
        get() = paiseToPlainRupees(lineFinalPricePaise)

    val lineGstRupeesString: String
        get() = paiseToPlainRupees(lineGstPaise)

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
