package com.erfangc.equity.valuation

data class ProcessTickerRequest(
    val ticker: String,
    val name: String? = null,
    val sector: String? = null,
    val industry: String? = null
)