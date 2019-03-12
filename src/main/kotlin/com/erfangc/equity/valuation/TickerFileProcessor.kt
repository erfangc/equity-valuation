package com.erfangc.equity.valuation

import com.erfangc.equity.valuation.computers.*
import com.erfangc.equity.valuation.services.Company
import com.erfangc.equity.valuation.services.CompanyService
import com.erfangc.equity.valuation.yahoo.YahooFinance
import com.erfangc.equity.valuation.yahoo.YahooFinanceRetriever
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.Closeable
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class TickerFileProcessor : Closeable {

    private val yahooFinanceRetriever = YahooFinanceRetriever()
    private val executor = Executors.newFixedThreadPool(1)
    private val companyService = CompanyService()
    val assumptions = Assumptions(riskFree = 0.0253, equityMarketPremium = 0.08210)
    private val derivedComputers = listOf(ImpliedGrowthRateComputer(), DCFComputer(assumptions))

    fun processFile(file: File) {
        val csvParser = CSVParser(file.bufferedReader(), CSVFormat.DEFAULT.withFirstRecordAsHeader())
        val futures = csvParser.records.map { record ->

            val ticker = record.get("Symbol")
            val name = record.get("Name")
            val sector = record.get("Sector")
            val industry = record.get("industry")

            // n/a usually implies mutual funds & trust entities that are not real companies
            if (sector != "n/a") {
                executor.submit {
                    try {
                        val start = System.nanoTime()
                        val yahooFinance = yahooFinanceRetriever.retrieve(ticker)
                        val company = Company(
                            ticker = ticker,
                            yahooFinance = yahooFinance,
                            name = name,
                            derived = runDerivedProcessorChain(yahooFinance),
                            sector = sector,
                            industry = industry
                        )
                        companyService.put(company)
                        val end = System.nanoTime()
                        logger.info(
                            "Finished processing $ticker ($name) in ${
                            TimeUnit.MILLISECONDS.convert(
                                end - start,
                                TimeUnit.NANOSECONDS
                            )
                            } ms"
                        )
                    } catch (e: Exception) {
                        logger.error("Error while processing $ticker ($name), sector=$sector: ${e.message}")
                    }
                }
            } else {
                CompletableFuture.completedFuture(null)
            }
        }
        futures.forEach { it.get() }
        csvParser.close()
    }

    private fun runDerivedProcessorChain(yahooFinance: YahooFinance): Derived {
        return derivedComputers.fold(Derived()) { derived, derivedComputer ->
            derivedComputer.compute(
                yahooFinance,
                derived
            )
        }
    }

    override fun close() {
        companyService.close()
    }

}