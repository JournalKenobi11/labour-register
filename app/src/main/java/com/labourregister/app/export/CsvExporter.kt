package com.labourregister.app.export

import com.labourregister.app.data.LabourerEntity
import com.labourregister.app.data.ProductQuoteEntity
import com.labourregister.app.data.ShopWithQuotes
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object CsvExporter {

    private const val CSV_HEADER = "name,skill,village,daily_charge_inr,mobile_number"

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
            val skillEscaped = escapeField(labourer.skill)
            val villageEscaped = escapeField(labourer.village)
            val dailyChargeEscaped = escapeField(labourer.dailyChargeRupeesString)
            val mobileEscaped = escapeField(labourer.mobileNumber)

            writer.write("$nameEscaped,$skillEscaped,$villageEscaped,$dailyChargeEscaped,$mobileEscaped")
            writer.write("\r\n")
        }

        writer.flush()
    }

    /**
     * Exports a list of ShopWithQuotes in a human-friendly sectioned format per shop:
     * - Distinct shop header banner with contact and mobile info
     * - Table columns for product quotes without repeating shop info per line
     * - Safe characters (underscores instead of '=', 'Excl./Incl. 18% GST' instead of '+18%', 'TOTAL:' instead of '---')
     * - Standard 'Rs.' currency prefix to prevent mojibake/corrupted glyphs across all CSV viewers
     * - Clean subtotal row per shop without grand summary
     */
    fun exportShopInquiries(shopsWithQuotes: List<ShopWithQuotes>, outputStream: OutputStream) {
        val bom = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())
        outputStream.write(bom)

        val writer = BufferedWriter(OutputStreamWriter(outputStream, StandardCharsets.UTF_8))

        val productHeader = "Product Name,Brand / Model,Quantity,Unit Price (INR),GST Status,18% GST (INR),Line Total (INR),Notes"
        val divider = "_________________________________________________________________________________________________________"

        for ((index, shopWithQuotes) in shopsWithQuotes.withIndex()) {
            val shop = shopWithQuotes.shop
            val quotes = shopWithQuotes.quotes

            val contactInfo = buildString {
                append("SHOP: ${shop.displayTitle}")
                if (shop.contactName.isNotBlank()) append(" | Contact: ${shop.contactName.trim()}")
                if (shop.mobileNumber.isNotBlank()) append(" | Mobile: ${shop.mobileNumber.trim()}")
            }

            // Section divider and Shop Banner (using underscores to avoid Excel/Sheets formula parse errors)
            writer.write(escapeField(divider))
            writer.write("\r\n")
            writer.write(escapeField(contactInfo))
            writer.write("\r\n")
            writer.write(escapeField(divider))
            writer.write("\r\n")

            if (quotes.isEmpty()) {
                writer.write("(No product quotes logged for this shop)")
                writer.write("\r\n")
            } else {
                writer.write(productHeader)
                writer.write("\r\n")

                for (quote in quotes) {
                    val prodEsc = escapeField(quote.productName)
                    val brandEsc = escapeField(quote.brandOrModel)
                    val qtyEsc = quote.quantity.toString()
                    val quotedPriceEsc = escapeField(quote.priceRupeesString)
                    val gstStatusEsc = escapeField(quote.gstExportStatus)
                    val lineGstEsc = escapeField(quote.lineGstRupeesString)
                    val lineFinalPriceEsc = escapeField(quote.lineFinalPriceRupeesString)
                    val notesEsc = escapeField(quote.notes)

                    writer.write("$prodEsc,$brandEsc,$qtyEsc,$quotedPriceEsc,$gstStatusEsc,$lineGstEsc,$lineFinalPriceEsc,$notesEsc")
                    writer.write("\r\n")
                }

                // Shop Subtotal Line (safe text, Rs. prefix for broad viewer compatibility, no formula characters)
                val shopTitleEsc = escapeField("TOTAL: ${shop.displayTitle}")
                val skuText = "${shopWithQuotes.skuCount} SKUs (${shopWithQuotes.totalUnitsCount} Units)"
                val totalGstText = "Total GST: Rs. ${shopWithQuotes.totalGstRupeesString}"
                val totalQuotationText = "Shop Quotation: Rs. ${shopWithQuotes.totalQuotationRupeesString}"

                writer.write("$shopTitleEsc,,$skuText,,,$totalGstText,$totalQuotationText,")
                writer.write("\r\n")
            }

            // Blank spacer between shops
            if (index < shopsWithQuotes.size - 1) {
                writer.write("\r\n")
            }
        }

        writer.flush()
    }

    /**
     * Escapes a CSV field according to RFC 4180.
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
