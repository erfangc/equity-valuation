package com.erfangc.equity.valuation.computers

import com.erfangc.equity.valuation.yahoo.YahooFinance
import org.slf4j.LoggerFactory
import java.text.NumberFormat

class ImpliedGrowthRateComputer : DerivedComputer {

    private val logger = LoggerFactory.getLogger(ImpliedGrowthRateComputer::class.java)

    override fun compute(yahooFinance: YahooFinance, derived: Derived): Derived {
        // calculate implied constant growth rate
        val marketRiskPremium = 0.08
        val summary = yahooFinance.summary
        val ticker = yahooFinance.ticker
        val beta = summary.beta3YMonthly
        return if (beta != null && summary.eps != null && summary.previousClose != null) {
            val r = beta * marketRiskPremium
            // assume EPS ~= FCF
            val D = summary.eps
            val P = summary.previousClose
            val g = r - D / P
            val percentInstance = NumberFormat.getPercentInstance()
            if (g.isNaN()) {
                throw RuntimeException("cannot compute implied constant growth rate for ${summary.ticker} D=$D, r=$r, P=$P, beta=$beta")
            }
            logger.info("$ticker implied constant growth = ${percentInstance.format(g)}, D=$D, r=$r, P=$P, beta=$beta, D/P=${D / P}")
            derived.copy(impliedConstantGrowth = g)
        } else {
            derived
        }


    }
}