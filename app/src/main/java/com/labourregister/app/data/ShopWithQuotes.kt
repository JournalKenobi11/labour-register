package com.labourregister.app.data

import androidx.room.Embedded
import androidx.room.Relation

data class ShopWithQuotes(
    @Embedded
    val shop: ShopEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "shopId"
    )
    val quotes: List<ProductQuoteEntity> = emptyList()
) {
    val skuCount: Int
        get() = quotes.size

    val totalUnitsCount: Int
        get() = quotes.sumOf { it.quantity.coerceAtLeast(1) }

    val totalQuotationPaise: Long
        get() = quotes.mapNotNull { it.lineFinalPricePaise }.sum()

    val formattedTotalQuotation: String
        get() = ProductQuoteEntity.formatPaise(totalQuotationPaise)

    val totalQuotationRupeesString: String
        get() = ProductQuoteEntity.paiseToPlainRupees(totalQuotationPaise)

    val totalGstPaise: Long
        get() = quotes.mapNotNull { it.lineGstPaise }.sum()

    val formattedTotalGst: String
        get() = ProductQuoteEntity.formatPaise(totalGstPaise)

    val totalGstRupeesString: String
        get() = ProductQuoteEntity.paiseToPlainRupees(totalGstPaise)

    val totalBasePaise: Long
        get() = quotes.mapNotNull { it.lineBasePaise }.sum()

    val formattedTotalBase: String
        get() = ProductQuoteEntity.formatPaise(totalBasePaise)
}

data class QuoteWithShop(
    val quote: ProductQuoteEntity,
    val shop: ShopEntity
)
