package com.labourregister.app

import com.labourregister.app.data.LabourerEntity
import com.labourregister.app.export.CsvExporter
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

class CsvExporterTest {

    @Test
    fun testEscapeField_nullOrEmpty() {
        assertEquals("", CsvExporter.escapeField(null))
        assertEquals("", CsvExporter.escapeField(""))
    }

    @Test
    fun testEscapeField_simpleText() {
        assertEquals("Ramesh Kumar", CsvExporter.escapeField("Ramesh Kumar"))
        assertEquals("9876543210", CsvExporter.escapeField("9876543210"))
    }

    @Test
    fun testEscapeField_withCommas() {
        assertEquals("\"Rampur, North\"", CsvExporter.escapeField("Rampur, North"))
    }

    @Test
    fun testEscapeField_withQuotes() {
        assertEquals("\"He said \"\"Hello\"\"\"", CsvExporter.escapeField("He said \"Hello\""))
    }

    @Test
    fun testEscapeField_withNewlines() {
        assertEquals("\"Line1\nLine2\"", CsvExporter.escapeField("Line1\nLine2"))
        assertEquals("\"Line1\r\nLine2\"", CsvExporter.escapeField("Line1\r\nLine2"))
    }

    @Test
    fun testExport_includesBomAndCorrectHeaders() {
        val stream = ByteArrayOutputStream()
        val labourers = listOf(
            LabourerEntity(
                id = 1,
                name = "Ramesh Kumar",
                village = "Rampur, Sector 4",
                dailyChargePaise = 50000, // ₹500
                mobileNumber = "+919876543210"
            ),
            LabourerEntity(
                id = 2,
                name = "Suresh \"The Mason\"",
                village = null,
                dailyChargePaise = 45050, // ₹450.50
                mobileNumber = "09876543210"
            ),
            LabourerEntity(
                id = 3,
                name = null,
                village = "Sitapur",
                dailyChargePaise = null,
                mobileNumber = null
            )
        )

        CsvExporter.export(labourers, stream)
        val bytes = stream.toByteArray()

        // Check UTF-8 BOM
        val expectedBom = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())
        assertArrayEquals(expectedBom, bytes.copyOfRange(0, 3))

        // Decode remaining bytes as UTF-8
        val text = String(bytes, 3, bytes.size - 3, StandardCharsets.UTF_8)
        val lines = text.split("\r\n").filter { it.isNotEmpty() }

        assertEquals(4, lines.size)
        // Header
        assertEquals("name,village,daily_charge_inr,mobile_number", lines[0])

        // Row 1
        assertEquals("Ramesh Kumar,\"Rampur, Sector 4\",500,+919876543210", lines[1])

        // Row 2 (with quotes escaping and preserved leading zero)
        assertEquals("\"Suresh \"\"The Mason\"\"\",,450.50,09876543210", lines[2])

        // Row 3 (with empty/null fields preserved as empty strings)
        assertEquals(",Sitapur,,", lines[3])
    }
}
