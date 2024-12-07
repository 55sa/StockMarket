package com.plcoding.stockmarketapp.data.remote.dto

data class GptRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>,
    val max_tokens: Int = 200,
    val temperature: Double = 0.7
)