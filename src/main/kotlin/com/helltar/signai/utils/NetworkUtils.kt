package com.helltar.signai.utils

import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import io.github.oshai.kotlinlogging.KotlinLogging

object NetworkUtils {

    private const val TIMEOUT = 180000
    private val log = KotlinLogging.logger {}

    fun httpGet(url: String, parameters: Parameters = listOf()): String {
        val responseResult =
            url.httpGet(parameters)
                .timeout(TIMEOUT)
                .timeoutRead(TIMEOUT)
                .response()

        val response = responseResult.second
        val json = response.data.decodeToString()

        if (!response.isSuccessful)
            throw Exception("[GET] request failed: ${response.statusCode} $url $json")

        log.debug { "${responseResult.first.url} --> $json" }

        return json
    }

    fun httpPost(url: String, headers: Map<String, String> = mapOf(), jsonBody: String): Response {
        val response =
            url.httpPost()
                .header(headers)
                .timeout(TIMEOUT)
                .timeoutRead(TIMEOUT)
                .jsonBody(jsonBody)
                .response().second

        if (!response.isSuccessful)
            throw Exception("[POST] request failed: ${response.statusCode} $url ${response.data.decodeToString()}")

        return response
    }

    fun httpPut(url: String, jsonBody: String) =
        url.httpPut()
            .jsonBody(jsonBody)
            .response().second.data.decodeToString()

    fun httpDelete(url: String, jsonBody: String) =
        url.httpDelete()
            .jsonBody(jsonBody)
            .response().second.data.decodeToString()
}
