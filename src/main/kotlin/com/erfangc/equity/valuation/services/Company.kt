package com.erfangc.equity.valuation.services

import com.erfangc.equity.valuation.computers.Derived
import com.erfangc.equity.valuation.yahoo.YahooFinance

data class Company(
    val ticker: String,
    val name: String,
    val sector: String? = null,
    val industry: String? = null,
    val yahooFinance: YahooFinance? = null,
    val derived: Derived = Derived()
)