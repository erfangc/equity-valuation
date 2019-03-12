package com.erfangc.equity.valuation.yahoo.financials

import com.erfangc.equity.valuation.yahoo.TableParser
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import us.codecraft.xsoup.Xsoup

class FinancialsRetriever {

    private val logger = LoggerFactory.getLogger(FinancialsRetriever::class.java)

    fun retrieveFinancials(ticker: String): List<Financial> {
        val incomeStatement = TableParser().parseTableWithColumnsAsDates(getIncomeStatementTable(ticker))
        val cashflowStatement = TableParser().parseTableWithColumnsAsDates(getCashflowStatementTable(ticker))
        val balanceSheet = TableParser().parseTableWithColumnsAsDates(getBalanceSheetTable(ticker))
        return incomeStatement.map { (date, metrics) ->
            Financial(
                date = date,
                incomeStatement = metrics.mapValues { it.value.toString().toDouble() * 1000.0 },
                balanceSheet = balanceSheet.getOrDefault(date, emptyMap()).mapValues { it.value.toString().toDouble() * 1000.0 },
                cashflowStatement = cashflowStatement.getOrDefault(date, emptyMap()).mapValues {
                    it.value.toString().toDouble() * 1000.0
                }
            )
        }
    }

    private fun getIncomeStatementTable(ticker: String): Element {
        logger.info("Retrieving income statement for $ticker ...")
        val incomeStatementDocument = Jsoup.connect("https://finance.yahoo.com/quote/$ticker/financials").get()
        return Xsoup
            .compile("//*[@id=\"Col1-1-Financials-Proxy\"]/section/div/table")
            .evaluate(incomeStatementDocument)
            .elements
            .first()
    }

    private fun getCashflowStatementTable(ticker: String): Element {
        logger.info("Retrieving cashflow statement for $ticker ...")
        val incomeStatementDocument = Jsoup.connect("https://finance.yahoo.com/quote/$ticker/cash-flow").get()
        return Xsoup
            .compile("//*[@id=\"Col1-1-Financials-Proxy\"]/section/div/table")
            .evaluate(incomeStatementDocument)
            .elements
            .first()
    }

    private fun getBalanceSheetTable(ticker: String): Element {
        logger.info("Retrieving balance sheet for $ticker ...")
        val incomeStatementDocument = Jsoup.connect("https://finance.yahoo.com/quote/$ticker/balance-sheet").get()
        return Xsoup
            .compile("//*[@id=\"Col1-1-Financials-Proxy\"]/section/div/table")
            .evaluate(incomeStatementDocument)
            .elements
            .first()
    }
}