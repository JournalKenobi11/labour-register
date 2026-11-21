package com.labourregister.app

import com.labourregister.app.data.ProductQuoteEntity
import com.labourregister.app.data.ShopEntity
import com.labourregister.app.data.ShopWithQuotes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductQuoteEntityTest {

    @Test
    fun testFormattedPrice() {
        val nullPrice = ProductQuoteEntity(id = 1, shopId = 1, pricePaise = null)
        assertEquals("-", nullPrice.formattedPrice)

        val wholeRupees = ProductQuoteEntity(id = 2, shopId = 1, pricePaise = 580000) // ₹5,800
        assertEquals("₹5800", wholeRupees.formattedPrice)

        val fractionalRupees = ProductQuoteEntity(id = 3, shopId = 1, pricePaise = 245050) // ₹2,450.50
        assertEquals("₹2450.50", fractionalRupees.formattedPrice)
    }

    @Test
    fun testPriceRupeesString() {
        val nullPrice = ProductQuoteEntity(id = 1, shopId = 1, pricePaise = null)
        assertEquals("", nullPrice.priceRupeesString)

        val wholeRupees = ProductQuoteEntity(id = 2, shopId = 1, pricePaise = 580000)
        assertEquals("5800", wholeRupees.priceRupeesString)

        val fractionalRupees = ProductQuoteEntity(id = 3, shopId = 1, pricePaise = 245050)
        assertEquals("2450.50", fractionalRupees.priceRupeesString)
    }

    @Test
    fun test18PercentGstCalculations() {
        // Excluded GST: 1000 + 18% = 1180
        val exclGst = ProductQuoteEntity(id = 1, shopId = 1, pricePaise = 100000, gstIncluded = false)
        assertEquals("+18% GST", exclGst.gstBadgeText)
        assertEquals(18000L, exclGst.gstAmountPaise) // ₹180
        assertEquals(118000L, exclGst.finalPricePaise) // ₹1180
        assertEquals("₹180", exclGst.formattedGstAmount)
        assertEquals("₹1180", exclGst.formattedFinalPrice)

        // Included GST: 5900 with 18% GST included -> base = 5000, GST = 900
        val inclGst = ProductQuoteEntity(id = 2, shopId = 1, pricePaise = 590000, gstIncluded = true)
        assertEquals("Incl. 18% GST", inclGst.gstBadgeText)
        assertEquals(90000L, inclGst.gstAmountPaise) // ₹900
        assertEquals(590000L, inclGst.finalPricePaise) // ₹5900
        assertEquals("₹900", inclGst.formattedGstAmount)
        assertEquals("₹5900", inclGst.formattedFinalPrice)
    }

    @Test
    fun testShopWithQuotesCalculations() {
        val shop = ShopEntity(id = 1, shopName = "Royal Hardware")
        val quote1 = ProductQuoteEntity(id = 1, shopId = 1, pricePaise = 100000, gstIncluded = false) // 1180 final
        val quote2 = ProductQuoteEntity(id = 2, shopId = 1, pricePaise = 590000, gstIncluded = true) // 5900 final

        val shopWithQuotes = ShopWithQuotes(shop = shop, quotes = listOf(quote1, quote2))
        assertEquals(2, shopWithQuotes.skuCount)
        assertEquals(708000L, shopWithQuotes.totalQuotationPaise) // 1180 + 5900 = 7080
        assertEquals("₹7080", shopWithQuotes.formattedTotalQuotation)
    }

    @Test
    fun testQuoteValidation() {
        val emptyQuote = ProductQuoteEntity(id = 1, shopId = 1, productName = "", pricePaise = null)
        assertFalse(emptyQuote.isValid)

        val blankNameQuote = ProductQuoteEntity(id = 2, shopId = 1, productName = "   ", pricePaise = null)
        assertFalse(blankNameQuote.isValid)

        val withNameOnly = ProductQuoteEntity(id = 3, shopId = 1, productName = "Cat6 Cable", pricePaise = null)
        assertTrue(withNameOnly.isValid)

        val withPriceOnly = ProductQuoteEntity(id = 4, shopId = 1, productName = "", pricePaise = 50000)
        assertTrue(withPriceOnly.isValid)

        val withBoth = ProductQuoteEntity(id = 5, shopId = 1, productName = "Cat6 Cable", pricePaise = 50000)
        assertTrue(withBoth.isValid)
    }

    @Test
    fun testShopEntityDisplayTitleAndBlank() {
        val emptyShop = ShopEntity(id = 10)
        assertTrue(emptyShop.isBlank)
        assertEquals("Shop #10", emptyShop.displayTitle)

        val namedShop = ShopEntity(id = 1, shopName = "Royal Electricals")
        assertFalse(namedShop.isBlank)
        assertEquals("Royal Electricals", namedShop.displayTitle)

        val contactOnlyShop = ShopEntity(id = 2, contactName = "Sunil Bhai")
        assertFalse(contactOnlyShop.isBlank)
        assertEquals("Sunil Bhai", contactOnlyShop.displayTitle)

        val locationOnlyShop = ShopEntity(id = 3, location = "Nehru Market")
        assertFalse(locationOnlyShop.isBlank)
        assertEquals("Nehru Market", locationOnlyShop.displayTitle)
    }
}
