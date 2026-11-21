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
                        quantity = 2,
                        pricePaise = 500000, // ₹5000 + 18% = ₹5900. 2 units = ₹11800 total, ₹1800 GST
                        gstIncluded = false,
                        notes = "Min 2 boxes"
                    ),
                    ProductQuoteEntity(
                        id = 102,
                        shopId = 1,
                        productName = "2MP Dome Camera",
                        brandOrModel = "Hikvision",
                        quantity = 4,
                        pricePaise = 118000, // ₹1180 incl. 18% = ₹1180. 4 units = ₹4720 total, ₹720 GST
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

        // Verify standard 11-column header
        assertTrue(text.contains("Shop Name,Contact Person,Mobile Number,Product Name,Brand / Model,Quantity,Unit Price (INR),GST Status,18% GST (INR),Line Total (INR),Notes"))

        // Verify Cat6 line with 2 units (Excl. 18% GST, pure numeric amounts)
        assertTrue(text.contains("Royal Hardware,Sunil Bhai,+919825012345,Cat6 Cable 305m,D-Link,2,5000,Excl. 18% GST,1800,11800,Min 2 boxes"))

        // Verify Dome Camera line with 4 units (Incl. 18% GST, pure numeric amounts)
        assertTrue(text.contains("Royal Hardware,Sunil Bhai,+919825012345,2MP Dome Camera,Hikvision,4,1180,Incl. 18% GST,720,4720,Night color"))

        // Verify Shop 1 subtotal with clean 11-columns, pure numbers, no leading '-' or '=' formula triggers, and no rupee symbols
        assertTrue(text.contains("TOTAL: Royal Hardware,Sunil Bhai,+919825012345,2 SKUs (6 Units),,6,,Total GST,2520,16520,"))

        // Verify Shop 2 row for empty quote
        assertTrue(text.contains("Shree Ram Sanitary,Ramesh,09876543210,(No product quotes logged),,,,,,,"))

        // Verify no formula injection trigger characters like ================= or ---
        assertFalse(text.contains("================="))
        assertFalse(text.contains("--- TOTAL:"))

        // Verify no raw rupee symbols in CSV numbers causing mojibake
        assertFalse(text.contains("₹"))

        // User explicitly specified: NO allshop summary at the bottom
        assertFalse(text.contains("ALL SHOPS GRAND SUMMARY"))
        assertFalse(text.contains("Combined Value"))
    }
}
