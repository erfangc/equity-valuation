package com.erfangc.equity.valuation.yahoo

import org.apache.commons.text.WordUtils
import org.jsoup.select.Elements
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDate.parse
import java.time.format.DateTimeFormatter
import java.util.*

object YahooFinanceUtils {

    fun formatLabel(str: String) = WordUtils.capitalizeFully(str).replace(" ", "")

    fun parseDouble(value: Any): Double? {
        return try {
            val valueAsString = value.toString().trim()
            val last = valueAsString.last()
            val multiplier = when (last) {
                'M' -> 1000000
                'B' -> 1000000000
                else -> 1
            }
            NumberFormat.getNumberInstance(java.util.Locale.US).parse(valueAsString).toDouble() * multiplier
        } catch (e: Exception) {
            null
        }
    }

    fun columnToDate(trs: Elements): Map<Int, LocalDate> {
        val firstRow = trs.first()
        val tds = firstRow.select("td")
        return tds
            .drop(1)
            .mapIndexed { idx, col ->
                idx to parse(col.text(), DateTimeFormatter.ofPattern("M/d/yyyy").withLocale(Locale.US))
            }
            .toMap()
    }
}