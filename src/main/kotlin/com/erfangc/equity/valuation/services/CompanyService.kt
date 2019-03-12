package com.erfangc.equity.valuation.services

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.http.HttpHost
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.get.GetRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType
import org.slf4j.LoggerFactory
import java.io.Closeable

class CompanyService : Closeable {

    private val objectMapper =
        jacksonObjectMapper()
            .findAndRegisterModules()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

    private val logger = LoggerFactory.getLogger(CompanyService::class.java)

    private val elasticsearch = RestHighLevelClient(
        RestClient.builder(
            HttpHost("localhost", 9200, "http")
        )
    )

    fun put(company: Company) {
        val json = objectMapper.writeValueAsString(company)
        elasticsearch.index(
            IndexRequest("companies", "_doc", company.ticker)
                .source(json, XContentType.JSON),
            RequestOptions.DEFAULT
        )
    }

    fun get(ticker: String): Company? {
        val response = elasticsearch.get(GetRequest("companies", "_doc", ticker), RequestOptions.DEFAULT)
        return try {
            objectMapper.readValue(response.sourceAsString)
        } catch (e: Exception) {
            logger.error("Error while retrieving $ticker, message: ${e.message}")
            null
        }
    }

    fun delete(ticker: String) {
        elasticsearch.delete(DeleteRequest("companies", "_doc", ticker), RequestOptions.DEFAULT)
    }

    override fun close() {
        elasticsearch.lowLevelClient.close()
    }
}