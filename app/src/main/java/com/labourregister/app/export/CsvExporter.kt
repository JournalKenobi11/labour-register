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
    private const val SHOP_CSV_HEADER = "Shop Name,Contact Person,Mobile Number,Product Name,Brand / Model,Quantity,Unit Price (INR),GST Status,18% GST (INR),Line Total (INR),Notes"

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
     * Exports a list of ShopWithQuotes in a robust, standard 11-column tabular format:
     * - Consistent 11 columns across all rows (Shop Name, Contact, Mobile, Product details, Line Total)
     * - Safe text without leading '=' / '+' / '-' to prevent formula injection (#NAME?, #ERROR!)
     * - Pure numerical values for financial amounts without currency symbols
     * - Clean subtotal row per shop without grand summary
     */
    fun exportShopInquiries(shopsWithQuotes: List<ShopWithQuotes>, outputStream: OutputStream) {
        val bom = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())
        outputStream.write(bom)

        val writer = BufferedWriter(OutputStreamWriter(outputStream, StandardCharsets.UTF_8))
        writer.write(SHOP_CSV_HEADER)
        writer.write("\r\n")

        for ((index, shopWithQuotes) in shopsWithQuotes.withIndex()) {
            val shop = shopWithQuotes.shop
            val quotes = shopWithQuotes.quotes

            val shopNameEsc = escapeField(shop.shopName)
            val contactEsc = escapeField(shop.contactName)
            val mobileEsc = escapeField(shop.mobileNumber)

            if (quotes.isEmpty()) {
                writer.write("$shopNameEsc,$contactEsc,$mobileEsc,(No product quotes logged),,,,,,,")
                writer.write("\r\n")
            } else {
                for (quote in quotes) {
                    val prodEsc = escapeField(quote.productName)
                    val brandEsc = escapeField(quote.brandOrModel)
                    val qtyEsc = quote.quantity.toString()
                    val quotedPriceEsc = escapeField(quote.priceRupeesString)
                    val gstStatusEsc = escapeField(quote.gstExportStatus)
                    val lineGstEsc = escapeField(quote.lineGstRupeesString)
                    val lineFinalPriceEsc = escapeField(quote.lineFinalPriceRupeesString)
                    val notesEsc = escapeField(quote.notes)

                    writer.write("$shopNameEsc,$contactEsc,$mobileEsc,$prodEsc,$brandEsc,$qtyEsc,$quotedPriceEsc,$gstStatusEsc,$lineGstEsc,$lineFinalPriceEsc,$notesEsc")
                    writer.write("\r\n")
                }

                // Shop Subtotal Line (clean 11 columns, pure numbers, no formula characters or rupee symbols)
                val shopTitleEsc = escapeField("TOTAL: ${shop.displayTitle}")
                val skuSummary = escapeField("${shopWithQuotes.skuCount} SKUs (${shopWithQuotes.totalUnitsCount} Units)")
                val totalQty = shopWithQuotes.totalUnitsCount.toString()
                val totalGst = shopWithQuotes.totalGstRupeesString
                val totalQuotation = shopWithQuotes.totalQuotationRupeesString

                writer.write("$shopTitleEsc,$contactEsc,$mobileEsc,$skuSummary,,$totalQty,,Total GST,$totalGst,$totalQuotation,")
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
