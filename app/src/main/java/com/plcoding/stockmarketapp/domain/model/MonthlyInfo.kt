package com.plcoding.stockmarketapp.domain.model

import java.time.LocalDate

data class MonthlyInfo(
    val date: LocalDate,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double
)