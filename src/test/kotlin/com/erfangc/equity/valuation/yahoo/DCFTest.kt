package com.erfangc.equity.valuation.yahoo

import com.erfangc.equity.valuation.computers.Assumptions
import com.erfangc.equity.valuation.computers.DCFComputer
import com.erfangc.equity.valuation.services.CompanyService
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

val dcfComputer = DCFComputer(Assumptions(riskFree = 0.015, equityMarketPremium = 0.075))
val svc = CompanyService()

fun main() {
    val ticker = "FB"
    val company = svc.get(ticker)!!
    val derived = dcfComputer.compute(yahooFinance = company.yahooFinance, derived = company.derived)
    val objectMapper = jacksonObjectMapper()
        .findAndRegisterModules()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .writerWithDefaultPrettyPrinter()
    svc.close()
    val json = objectMapper.writeValueAsString(derived)
    println(json)
}