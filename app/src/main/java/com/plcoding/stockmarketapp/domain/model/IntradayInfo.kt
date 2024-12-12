
package com.plcoding.stockmarketapp.domain.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class IntradayInfo(
    val date: LocalDateTime,
    val close: Double,
    val volume: Double,
    val low: Double,
    val high: Double
)


fun List<IntradayInfo>.toWeeklySummaries(): Map<LocalDateTime, IntradayInfo> {
    return this.groupBy { info ->
        info.date.with(java.time.DayOfWeek.MONDAY)
    }.mapValues { (weekStart, entries) ->
        IntradayInfo(
            date = weekStart,
            close = entries.last().close,
            volume = entries.sumOf { it.volume },
            low = entries.minOf { it.low },
            high = entries.maxOf { it.high }
        )
    }
}


fun List<IntradayInfo>.toMonthlySummaries(): Map<LocalDateTime, IntradayInfo> {
    return this.groupBy { info ->
        info.date.withDayOfMonth(1)
    }.mapValues { (monthStart, entries) ->
        IntradayInfo(
            date = monthStart,
            close = entries.last().close,
            volume = entries.sumOf { it.volume },
            low = entries.minOf { it.low },
            high = entries.maxOf { it.high }
        )
    }
}

