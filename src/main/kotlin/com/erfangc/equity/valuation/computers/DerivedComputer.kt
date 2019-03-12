package com.erfangc.equity.valuation.computers

import com.erfangc.equity.valuation.yahoo.YahooFinance

interface DerivedComputer {
    fun compute(yahooFinance: YahooFinance, derived: Derived = Derived()): Derived
}