package com.labourregister.app.export

import com.labourregister.app.data.LabourerEntity
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object CsvExporter {

    private val CSV_HEADER = "name,village,daily_charge_inr,mobile_number"

    /**
     * Exports a list of LabourerEntity records to the given OutputStream in CSV format.
     * Writes UTF-8 BOM, the exact header line, and escaped records.
     */
    fun export(labourers: List<LabourerEntity>, outputStream: OutputStream) {
        // UTF-8 BOM bytes: 0xEF, 0xBB, 0xBF
        val bom = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())
        outputStream.write(bom)

        val writer = BufferedWriter(OutputStreamWriter(outputStream, StandardCharsets.UTF_8))
        writer.write(CSV_HEADER)
        writer.write("\r\n")

        for (labourer in labourers) {
            val nameEscaped = escapeField(labourer.name)
            val villageEscaped = escapeField(labourer.village)
            val dailyChargeEscaped = escapeField(labourer.dailyChargeRupeesString)
            val mobileEscaped = escapeField(labourer.mobileNumber)

            writer.write("$nameEscaped,$villageEscaped,$dailyChargeEscaped,$mobileEscaped")
            writer.write("\r\n")
        }

        writer.flush()
    }

    /**
     * Escapes a CSV field according to RFC 4180:
     * - Null or empty returns empty string.
     * - If contains comma, quote, newline or carriage return, wrapped in quotes
     *   with any internal quote escaped as double quotes ("").
     */
    fun escapeField(value: String?): String {
        if (value == null || value.isEmpty()) {
            return ""
        }
        val needsQuotes = value.contains(',') ||
                value.contains('"') ||
                value.contains('\n') ||
                value.contains('\r')

        return if (needsQuotes) {
            val escapedQuotes = value.replace("\"", "\"\"")
            "\"$escapedQuotes\""
        } else {
            value
        }
    }
}
