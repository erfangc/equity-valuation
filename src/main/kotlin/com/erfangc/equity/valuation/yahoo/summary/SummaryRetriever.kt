package com.erfangc.equity.valuation.yahoo.summary

import com.erfangc.equity.valuation.yahoo.YahooFinanceUtils.parseDouble
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import us.codecraft.xsoup.Xsoup

class SummaryRetriever {

    private val logger = LoggerFactory.getLogger(SummaryRetriever::class.java)

    fun retrieveSummary(ticker: String): Summary {
        logger.info("Retrieving summary for $ticker...")
        val summary = Jsoup
            .connect("https://finance.yahoo.com/quote/$ticker")
            .get()

        logger.info("Retrieving profile for $ticker...")
        val profile = Jsoup
            .connect("https://finance.yahoo.com/quote/$ticker/profile")
            .get()

        // Table 1 (with quotes etc ...)
        val previousClose = parsePreviousClose(summary)

        // Table 2 (with some other attrs like beta and market cap)
        val rows = Xsoup
            .compile("//*[@id=\"quote-summary\"]/div[2]/table/tbody/tr")
            .evaluate(summary)
            .elements
        val marketCap = searchTableRows(rows, "Market Cap")
        val beta3YMonthly = searchTableRows(rows, "Beta (3Y Monthly)")
        val peRatio = searchTableRows(rows, "PE Ratio (TTM)")
        val epsTTM = searchTableRows(rows, "EPS (TTM)")
        val sector = profile.select(
            "#Col1-0-Profile-Proxy > section > div.asset-profile-container > div > div > p:nth-child(2) > span:nth-child(2)"
        ).text()
        val industry = profile.select(
            "#Col1-0-Profile-Proxy > section > div.asset-profile-container > div > div > p:nth-child(2) > span:nth-child(5)"
        ).text()

        return Summary(
            ticker = ticker,
            name = ticker,
            previousClose = previousClose,
            sector = sector,
            beta3YMonthly = beta3YMonthly,
            eps = epsTTM,
            industry = industry,
            marketCap = marketCap,
            peRatio = peRatio
        )

    }

    private fun parsePreviousClose(summary: Document?): Double? {
        val trs = Xsoup
            .compile("//*[@id=\"quote-summary\"]/div[1]/table/tbody/tr")
            .evaluate(summary)
            .elements
        val cellToFind = "Previous Close"
        return searchTableRows(trs, cellToFind)
    }

    private fun searchTableRows(trs: Elements, cellToFind: String): Double? {
        return trs
            .find { tr ->
                tr.select("td").first().text() == cellToFind
            }
            ?.select("td")
            ?.last()
            ?.text()
            ?.let { parseDouble(it) }
    }
}
