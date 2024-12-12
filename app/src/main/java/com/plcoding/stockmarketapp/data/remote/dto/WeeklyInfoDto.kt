package com.plcoding.stockmarketapp.data.remote.dto

data class WeeklyInfoDto(
    val timestamp: String,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double
)