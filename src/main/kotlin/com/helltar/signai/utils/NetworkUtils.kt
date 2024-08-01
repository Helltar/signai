package com.helltar.signai.utils

import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut

object NetworkUtils {

    private const val TIMEOUT = 180000

    fun httpGet(url: String) =
        url.httpGet()
            .timeout(TIMEOUT)
            .timeoutRead(TIMEOUT)
            .response().second.data.decodeToString()

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