package com.erfangc.equity.valuation.yahoo

import com.erfangc.equity.valuation.yahoo.financials.FinancialsRetriever
import com.erfangc.equity.valuation.yahoo.summary.Summary
import com.erfangc.equity.valuation.yahoo.summary.SummaryRetriever
import org.slf4j.LoggerFactory
import java.lang.RuntimeException
import java.text.NumberFormat

class YahooFinanceRetriever {

    fun retrieve(ticker: String): YahooFinance {
        val summaryRetriever = SummaryRetriever()
        val financialsRetriever = FinancialsRetriever()
        val summary = summaryRetriever.retrieveSummary(ticker)
        val financials = financialsRetriever.retrieveFinancials(ticker)
        return YahooFinance(
            ticker = ticker,
            summary = summary,
            financials = financials
        )
    }

}