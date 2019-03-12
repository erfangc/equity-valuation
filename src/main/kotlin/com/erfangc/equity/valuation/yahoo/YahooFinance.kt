package com.erfangc.equity.valuation.yahoo

import com.erfangc.equity.valuation.yahoo.financials.Financial
import com.erfangc.equity.valuation.yahoo.summary.Summary

data class YahooFinance(
    val ticker: String,
    val summary: Summary,
    val financials: List<Financial>
)