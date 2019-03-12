package com.erfangc.equity.valuation

import org.slf4j.LoggerFactory
import java.io.File

val logger = LoggerFactory.getLogger("Main")!!

fun main() {
    val fileProcessor = TickerFileProcessor()
    listOf(
        "NYSE.csv"
        ,"NASDAQ.csv"
    )
        .forEach {
            val file = File(it)
            logger.info("Processing tickers from ${file.absolutePath}")
            fileProcessor.processFile(file)
            logger.info("Finished processing tickers from ${file.absolutePath}")
        }
    fileProcessor.close()
}
