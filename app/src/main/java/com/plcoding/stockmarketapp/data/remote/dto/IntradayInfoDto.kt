package com.plcoding.stockmarketapp.data.remote.dto

import java.time.LocalDateTime

data class IntradayInfoDto(
    val timestamp: String,
    val close: Double,
    val volume: Double,
    val low: Double,
    val high: Double
)
