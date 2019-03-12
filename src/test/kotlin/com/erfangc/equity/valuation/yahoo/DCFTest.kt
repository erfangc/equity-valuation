package com.erfangc.equity.valuation.yahoo

import com.erfangc.equity.valuation.computers.Assumptions
import com.erfangc.equity.valuation.computers.DCFComputer
import com.erfangc.equity.valuation.logger
import com.erfangc.equity.valuation.services.CompanyService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File

// risk free rate == T-bill yield 1 year as of 3/12
// mrp = average of earning yields from universe of securities
val assumptions = Assumptions(riskFree = 0.0253, equityMarketPremium = 0.08210)
val dcfComputer = DCFComputer(assumptions)
val svc = CompanyService()

fun main() {
    listOf(
        "NYSE.csv"
        , "NASDAQ.csv"
    )
        .forEach {
            val file = File(it)
            logger.info("Processing tickers from ${file.absolutePath}")
            val csvParser = CSVParser(file.bufferedReader(), CSVFormat.DEFAULT.withFirstRecordAsHeader())
            csvParser.records.map { record ->
                val ticker = record.get("Symbol")
                logger.info("Running ticker $ticker")
                try {
                    runDCFForTicker(ticker)
                } catch (e: Exception) {
                    logger.error("Error running ticker $ticker, error: ${e.message}")
                }

            }
            csvParser.close()
            logger.info("Finished processing tickers from ${file.absolutePath}")
        }
    svc.close()
}

private fun runDCFForTicker(ticker: String) {
    val company = svc.get(ticker)
    if (company != null) {
        val derived = dcfComputer.compute(yahooFinance = company.yahooFinance, derived = company.derived)
        svc.put(company.copy(derived = derived))
        logger.info("Finished ticker $ticker")
    } else {
        logger.info("Skipping ticker $ticker")
    }
}