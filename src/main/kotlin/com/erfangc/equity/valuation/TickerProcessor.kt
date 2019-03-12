package com.erfangc.equity.valuation

import com.erfangc.equity.valuation.computers.Assumptions
import com.erfangc.equity.valuation.computers.DCFComputer
import com.erfangc.equity.valuation.computers.Derived
import com.erfangc.equity.valuation.computers.ImpliedGrowthRateComputer
import com.erfangc.equity.valuation.services.Company
import com.erfangc.equity.valuation.services.CompanyService
import com.erfangc.equity.valuation.yahoo.YahooFinance
import com.erfangc.equity.valuation.yahoo.YahooFinanceRetriever
import org.apache.http.HttpHost
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.util.concurrent.TimeUnit

class TickerProcessor(
    private val companyService: CompanyService,
    private val yahooFinanceRetriever: YahooFinanceRetriever = YahooFinanceRetriever()
) : Closeable {

    companion object {
        fun defaultInstance(): TickerProcessor {
            return TickerProcessor(companyService = CompanyService(HttpHost("localhost", 9200, "http")))
        }
    }

    private val logger = LoggerFactory.getLogger(TickerProcessor::class.java)
    private val assumptions = Assumptions(riskFree = 0.0253, equityMarketPremium = 0.08210)
    private val derivedComputers = listOf(ImpliedGrowthRateComputer(), DCFComputer(assumptions))

    /**
     * Process the ticker and upload results via Company Service
     */
    fun processTicker(request: ProcessTickerRequest) {
        val (ticker, name, sector, industry) = request
        try {
            val start = System.nanoTime()
            val yahooFinance = yahooFinanceRetriever.retrieve(ticker)
            val company = Company(
                ticker = ticker,
                yahooFinance = yahooFinance,
                name = name ?: ticker,
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