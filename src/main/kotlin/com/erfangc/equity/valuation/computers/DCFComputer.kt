package com.erfangc.equity.valuation.computers

import com.erfangc.equity.valuation.yahoo.YahooFinance
import com.erfangc.equity.valuation.yahoo.financials.Financial.Companion.CashflowStatement.CapitalExpenditures
import com.erfangc.equity.valuation.yahoo.financials.Financial.Companion.CashflowStatement.Depreciation
import com.erfangc.equity.valuation.yahoo.financials.Financial.Companion.CashflowStatement.NetBorrowings
import com.erfangc.equity.valuation.yahoo.financials.Financial.Companion.IncomeStatement.NetIncome

data class Assumptions(val riskFree: Double, val equityMarketPremium: Double)

class DCFComputer(private val assumptions: Assumptions) : DerivedComputer {
    override fun compute(yahooFinance: YahooFinance, derived: Derived): Derived {
        val (
            _,
            incomeStatement,
            _,
            cashflowStatement
        ) = yahooFinance.latestFinancial()
        val beta = yahooFinance.summary.beta3YMonthly
        val marketCap = yahooFinance.summary.marketCap

        val netIncome = incomeStatement[NetIncome]

        val rf = assumptions.riskFree
        val rm = assumptions.equityMarketPremium

        return if (netIncome != null && beta != null && marketCap != null) {
            val depreciation = cashflowStatement[Depreciation] ?: 0.0
            val capex = (cashflowStatement[CapitalExpenditures] ?: 0.0)
            val netBorrowing = cashflowStatement[NetBorrowings] ?: 0.0
            val fcfe = netIncome + depreciation - capex + netBorrowing
            val r = rf + beta * (rm - rf)
            val g = r - fcfe * 1000 / marketCap
            derived.copy(fcfe = fcfe * 1000, impliedConstantFcfeGrowth = g)
        } else {
            derived
        }
    }

}