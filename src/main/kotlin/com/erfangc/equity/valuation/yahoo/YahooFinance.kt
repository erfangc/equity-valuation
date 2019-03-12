package com.erfangc.equity.valuation.yahoo

import com.erfangc.equity.valuation.yahoo.financials.Financial
import com.erfangc.equity.valuation.yahoo.summary.Summary
import java.time.Instant

data class YahooFinance(
    val ticker: String,
    val summary: Summary,
    val financials: List<Financial>,
    val lastUpdated: Instant
) {

    fun latestFinancial(): Financial {
        return financials.sortedByDescending { it.date }.first()
    }

    fun earliestFinancial(): Financial {
        return financials.sortedBy { it.date }.first()
    }

}