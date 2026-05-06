package com.example.campusiq.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object AIService {

    private const val API_KEY = "YOUR_GROQ_KEY_HERE"
    private const val API_URL = "https://api.groq.com/openai/v1/chat/completions"
    private const val MODEL   = "llama-3.3-70b-versatile"

    suspend fun getResponse(
        systemPrompt: String,
        userMessage: String,
        conversationHistory: List<Pair<String, String>> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        try {
            val url  = URL(API_URL)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Authorization", "Bearer $API_KEY")
            conn.doOutput = true
            conn.connectTimeout = 15000
            conn.readTimeout    = 30000

            val messages = JSONArray()

            messages.put(JSONObject().apply {
                put("role", "system")
                put("content", systemPrompt)
            })

            conversationHistory.forEach { (role, content) ->
                messages.put(JSONObject().apply {
                    put("role", role)
                    put("content", content)
                })
            }

            messages.put(JSONObject().apply {
                put("role", "user")
                put("content", userMessage)
            })

            val body = JSONObject().apply {
                put("model", MODEL)
                put("messages", messages)
                put("max_tokens", 500)
                put("temperature", 0.7)
            }

            OutputStreamWriter(conn.outputStream).use {
                it.write(body.toString())
            }

            val responseCode = conn.responseCode
            if (responseCode != 200) {
                val error = BufferedReader(
                    InputStreamReader(conn.errorStream)
                ).use { it.readText() }
                return@withContext "Error $responseCode: $error"
            }

            val response = BufferedReader(
                InputStreamReader(conn.inputStream)
            ).use { it.readText() }

            val json = JSONObject(response)
            json.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")

        } catch (e: Exception) {
            "Sorry, couldn't get a response. Check your internet and try again."
        }
    }
}