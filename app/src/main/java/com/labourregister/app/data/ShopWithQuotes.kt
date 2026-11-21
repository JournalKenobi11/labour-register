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

    val totalQuotationPaise: Long
        get() = quotes.mapNotNull { it.finalPricePaise }.sum()

    val formattedTotalQuotation: String
        get() = ProductQuoteEntity.formatPaise(totalQuotationPaise)

    val totalGstPaise: Long
        get() = quotes.mapNotNull { it.gstAmountPaise }.sum()

    val formattedTotalGst: String
        get() = ProductQuoteEntity.formatPaise(totalGstPaise)

    val totalBasePaise: Long
        get() = quotes.mapNotNull { it.basePricePaise }.sum()

    val formattedTotalBase: String
        get() = ProductQuoteEntity.formatPaise(totalBasePaise)
}

data class QuoteWithShop(
    val quote: ProductQuoteEntity,
    val shop: ShopEntity
)
