package com.erfangc.equity.valuation.services

import com.erfangc.equity.valuation.computers.Derived
import com.erfangc.equity.valuation.yahoo.YahooFinance

data class Company(val ticker: String, val yahooFinance: YahooFinance, val derived: Derived = Derived())