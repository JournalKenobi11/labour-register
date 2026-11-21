package com.labourregister.app

import com.labourregister.app.data.ProductQuoteEntity
import com.labourregister.app.data.ShopEntity
import com.labourregister.app.data.ShopWithQuotes
import com.labourregister.app.export.CsvExporter
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

class ShopInquiryCsvExporterTest {

    @Test
    fun testExportShopInquiries_readableMultiShopSections() {
        val stream = ByteArrayOutputStream()
        val shopsWithQuotes = listOf(
            ShopWithQuotes(
                shop = ShopEntity(
                    id = 1,
                    shopName = "Royal Hardware",
                    contactName = "Sunil Bhai",
                    mobileNumber = "+919825012345"
                ),
                quotes = listOf(
                    ProductQuoteEntity(
                        id = 101,
                        shopId = 1,
                        productName = "Cat6 Cable 305m",
                        brandOrModel = "D-Link",
                        pricePaise = 500000, // ₹5000 + 18% = ₹5900
                        gstIncluded = false,
                        notes = "Min 2 boxes"
                    ),
                    ProductQuoteEntity(
                        id = 102,
                        shopId = 1,
                        productName = "2MP Dome Camera",
                        brandOrModel = "Hikvision",
                        pricePaise = 118000, // ₹1180 incl. 18% = ₹1180
                        gstIncluded = true,
                        notes = "Night color"
                    )
                )
            ),
            ShopWithQuotes(
                shop = ShopEntity(
                    id = 2,
                    shopName = "Shree Ram Sanitary",
                    contactName = "Ramesh",
                    mobileNumber = "09876543210"
                ),
                quotes = emptyList() // Shop with zero quotes
            )
        )

        CsvExporter.exportShopInquiries(shopsWithQuotes, stream)
        val bytes = stream.toByteArray()

        // Check UTF-8 BOM
        val expectedBom = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())
        assertArrayEquals(expectedBom, bytes.copyOfRange(0, 3))

        // Decode text
        val text = String(bytes, 3, bytes.size - 3, StandardCharsets.UTF_8)

        // Verify Shop 1 banner
        assertTrue(text.contains("SHOP: Royal Hardware | Contact: Sunil Bhai | Mobile: +919825012345"))
        // Verify product columns
        assertTrue(text.contains("Product Name,Brand / Model,Quoted Price (INR),GST Status,18% GST (INR),Final Landed Price (INR),Notes"))
        // Verify Cat6 line with +18% GST calculation (5000 -> 900 GST -> 5900 final)
        assertTrue(text.contains("Cat6 Cable 305m,D-Link,5000,+18% GST,900,5900,Min 2 boxes"))
        // Verify Dome Camera line with Incl. 18% GST calculation (1180 -> 180 GST -> 1180 final)
        assertTrue(text.contains("2MP Dome Camera,Hikvision,1180,Incl. 18% GST,180,1180,Night color"))
        // Verify Shop 1 subtotal
        assertTrue(text.contains("--- TOTAL: Royal Hardware,,2 SKUs,,Total GST: ₹1080,Shop Quotation: ₹7080,"))

        // Verify Shop 2 banner and empty quote message
        assertTrue(text.contains("SHOP: Shree Ram Sanitary | Contact: Ramesh | Mobile: 09876543210"))
        assertTrue(text.contains("(No product quotes logged for this shop)"))

        // User explicitly specified: NO allshop summary at the bottom
        assertFalse(text.contains("ALL SHOPS GRAND SUMMARY"))
        assertFalse(text.contains("Combined Value"))
    }
}
