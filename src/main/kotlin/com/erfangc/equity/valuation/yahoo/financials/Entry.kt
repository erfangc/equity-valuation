package com.erfangc.equity.valuation.yahoo.financials

import java.time.LocalDate

internal data class Entry(val date: LocalDate, val value: Any, val label: String)