
package com.plcoding.stockmarketapp.data.remote.dto

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Represents an individual intraday data point with timestamp, close, volume, low, and high values.
 */
data class IntradayInfoDto(
    val timestamp: String,
    val close: Double,
    val volume: Double,
    val low: Double,
    val high: Double
)

/**
 * Aggregates intraday data into weekly summaries.
 */
fun List<IntradayInfoDto>.toWeeklyIntradayInfo(): Map<LocalDateTime, IntradayInfoDto> {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    return this.groupBy { dto ->
        val dateTime = LocalDateTime.parse(dto.timestamp, formatter)
        dateTime.with(java.time.DayOfWeek.MONDAY) // Group by the Monday of the week
    }.mapValues { (weekStart, entries) ->
        IntradayInfoDto(
            timestamp = weekStart.toString(),
            close = entries.last().close,
            volume = entries.sumOf { it.volume },
            low = entries.minOf { it.low },
            high = entries.maxOf { it.high }
        )
    }
}

/**
 * Aggregates intraday data into monthly summaries.
 */
fun List<IntradayInfoDto>.toMonthlyIntradayInfo(): Map<LocalDateTime, IntradayInfoDto> {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    return this.groupBy { dto ->
        val dateTime = LocalDateTime.parse(dto.timestamp, formatter)
        dateTime.withDayOfMonth(1) // Group by the first day of the month
    }.mapValues { (monthStart, entries) ->
        IntradayInfoDto(
            timestamp = monthStart.toString(),
            close = entries.last().close,
            volume = entries.sumOf { it.volume },
            low = entries.minOf { it.low },
            high = entries.maxOf { it.high }
        )
    }
}


/**
 * Aggregates intraday data into daily summaries.
 */
fun List<IntradayInfoDto>.toDailyIntradayInfo(): Map<LocalDateTime, IntradayInfoDto> {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    return this.groupBy { dto ->
        val dateTime = LocalDateTime.parse(dto.timestamp, formatter)
        dateTime.toLocalDate().atStartOfDay() // Group by the start of the day
    }.mapValues { (dayStart, entries) ->
        IntradayInfoDto(
            timestamp = dayStart.toString(),
            close = entries.last().close,
            volume = entries.sumOf { it.volume },
            low = entries.minOf { it.low },
            high = entries.maxOf { it.high }
        )
    }
}
