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
    fun test18PercentGstAndQuantityCalculations() {
        // 4 cameras @ ₹1000 each + 18% GST (unit final = 1180)
        val fourCameras = ProductQuoteEntity(
            id = 1,
            shopId = 1,
            quantity = 4,
            pricePaise = 100000,
            gstIncluded = false
        )
        assertEquals("+18% GST", fourCameras.gstBadgeText)
        assertEquals("Excl. 18% GST", fourCameras.gstExportStatus)
        assertEquals(4, fourCameras.quantity)
        assertEquals(18000L, fourCameras.unitGstPaise) // ₹180 per unit
        assertEquals(118000L, fourCameras.unitFinalPricePaise) // ₹1180 per unit
        assertEquals(72000L, fourCameras.lineGstPaise) // 4 * ₹180 = ₹720
        assertEquals(472000L, fourCameras.lineFinalPricePaise) // 4 * ₹1180 = ₹4720
        assertEquals("₹4720", fourCameras.formattedLineFinalPrice)

        // 2 DVRs @ ₹5900 each Incl. 18% GST (unit final = 5900, unit GST = 900)
        val twoDvrs = ProductQuoteEntity(
            id = 2,
            shopId = 1,
            quantity = 2,
            pricePaise = 590000,
            gstIncluded = true
        )
        assertEquals("Incl. 18% GST", twoDvrs.gstBadgeText)
        assertEquals("Incl. 18% GST", twoDvrs.gstExportStatus)
        assertEquals(2, twoDvrs.quantity)
        assertEquals(90000L, twoDvrs.unitGstPaise) // ₹900 per unit
        assertEquals(590000L, twoDvrs.unitFinalPricePaise) // ₹5900 per unit
        assertEquals(180000L, twoDvrs.lineGstPaise) // 2 * ₹900 = ₹1800
        assertEquals(1180000L, twoDvrs.lineFinalPricePaise) // 2 * ₹5900 = ₹11800
        assertEquals("₹11800", twoDvrs.formattedLineFinalPrice)
    }

    @Test
    fun testShopWithQuotesTotalsAndUnitCount() {
        val shop = ShopEntity(id = 1, shopName = "Royal Hardware")
        val quote1 = ProductQuoteEntity(id = 1, shopId = 1, quantity = 4, pricePaise = 100000, gstIncluded = false) // 4 * 1180 = 4720
        val quote2 = ProductQuoteEntity(id = 2, shopId = 1, quantity = 2, pricePaise = 590000, gstIncluded = true) // 2 * 5900 = 11800

        val shopWithQuotes = ShopWithQuotes(shop = shop, quotes = listOf(quote1, quote2))
        assertEquals(2, shopWithQuotes.skuCount) // 2 distinct products / SKUs
        assertEquals(6, shopWithQuotes.totalUnitsCount) // 4 cameras + 2 DVRs = 6 units
        assertEquals(1652000L, shopWithQuotes.totalQuotationPaise) // 4720 + 11800 = 16520
        assertEquals("₹16520", shopWithQuotes.formattedTotalQuotation)
        assertEquals("16520", shopWithQuotes.totalQuotationRupeesString)
        assertEquals("2520", shopWithQuotes.totalGstRupeesString)
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
    }
}
