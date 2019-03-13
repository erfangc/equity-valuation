package com.erfangc.equity.valuation.yahoo

import com.erfangc.equity.valuation.ProcessTickerRequest
import com.erfangc.equity.valuation.TickerProcessor
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File

fun main() {
    val tickerProcessor = TickerProcessor.defaultInstance()
    listOf(
        "NASDAQ.csv", "NYSE.csv"
    ).forEach { record ->
        val file = File(record)
        val csvParser = CSVParser(file.bufferedReader(), CSVFormat.DEFAULT.withFirstRecordAsHeader())
        csvParser.forEach {
            val ticker = it.get("Symbol")
            val name = it.get("Name")
            val sector = it.get("Sector")
            val industry = it.get("industry")
            if (sector != "n/a") {
                val request = ProcessTickerRequest(ticker = ticker, name = name, sector = sector, industry = industry)
                tickerProcessor.processTicker(request = request)
            }
        }
        csvParser.close()
    }
    tickerProcessor.close()
}