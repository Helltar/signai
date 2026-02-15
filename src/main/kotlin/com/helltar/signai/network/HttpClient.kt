package com.helltar.signai.network

interface HttpClient {

    suspend fun get(url: String, parameters: List<Pair<String, String>> = listOf()): String
    suspend fun post(url: String, headers: Map<String, String> = mapOf(), body: String): String
    suspend fun put(url: String, body: String): String
    suspend fun delete(url: String, body: String): String
}
