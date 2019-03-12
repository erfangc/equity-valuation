package com.erfangc.equity.valuation.computers

data class Derived(
    val impliedConstantGrowth: Double? = null,
    val fcfe: Double? = null,
    val impliedConstantFcfeGrowth: Double? = null
)