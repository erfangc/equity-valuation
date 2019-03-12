package com.erfangc.equity.valuation.yahoo

import com.erfangc.equity.valuation.yahoo.YahooFinanceUtils.formatLabel
import com.erfangc.equity.valuation.yahoo.YahooFinanceUtils.parseDouble
import org.jsoup.nodes.Element
import java.time.LocalDate

class TableParser {
    fun parseTableWithColumnsAsDates(table: Element): DatedContent {
        if (table.tagName() != "table") {
            throw RuntimeException("element must be a table but was ${table.tagName()} instead")
        }
        val rows = table.select("tr")
        val columnToDate = YahooFinanceUtils.columnToDate(rows)
        return rows
            .asSequence()
            // the first row is presumed to label the dates
            .drop(1)
            // skip rows with colspan defined, those usually do not correspond with what we need
            .filter { tr -> tr.attr("colspan").isBlank() }
            .map { tr ->
                val tds = tr.select("td")
                val label = tds.first().text()
                tds
                    .drop(1)
                    .mapIndexed { idx, td ->
                        val date = columnToDate.getValue(idx)
                        Triple(label, date, td.text())
                    }
            }
            .flatten()
            .groupBy { it.second }
            .map { entry ->
                entry.key to entry
                    .value
                    .map {
                        formatLabel(it.first) to parseDouble(it.third)
                    }
                    .filter { it.second != null }
                    .map { it.first to it.second!! }
                    .toMap()
            }
            .toList()
            .toMap()
    }
}

/**
 * Each row an key value pair
 */
typealias TableEntity = Map<String, Any>

typealias DatedContent = Map<LocalDate, TableEntity>

fun DatedContent.latest(): TableEntity {
    return this.getValue(this.keys.sortedDescending().first())
}

fun DatedContent.earliest(): TableEntity {
    return this.getValue(this.keys.sorted().first())
}