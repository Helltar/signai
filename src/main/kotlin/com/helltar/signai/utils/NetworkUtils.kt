package com.helltar.signai.utils

import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import org.slf4j.LoggerFactory

object NetworkUtils {

    private const val TIMEOUT = 180000

    private val log = LoggerFactory.getLogger(javaClass)

    fun httpGet(url: String, parameters: Parameters = listOf()): String {
        val response =
            url.httpGet(parameters)
                .timeout(TIMEOUT)
                .timeoutRead(TIMEOUT)
                .response().second

        val json = response.data.decodeToString()

        log.debug("$url --> $json")

        return json
    }

    fun httpPost(url: String, headers: Map<String, String> = mapOf(), jsonBody: String) =
        url.httpPost()
            .header(headers)
            .timeout(TIMEOUT)
            .timeoutRead(TIMEOUT)
            .jsonBody(jsonBody)
            .response().second

    fun httpPut(url: String, jsonBody: String) =
        url.httpPut()
            .jsonBody(jsonBody)
            .response().second.data.decodeToString()

    fun httpDelete(url: String, jsonBody: String) =
        url.httpDelete()
            .jsonBody(jsonBody)
            .response().second.data.decodeToString()
}