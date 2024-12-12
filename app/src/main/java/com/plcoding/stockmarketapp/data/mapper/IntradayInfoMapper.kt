
package com.plcoding.stockmarketapp.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.plcoding.stockmarketapp.data.remote.dto.IntradayInfoDto
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
fun IntradayInfoDto.toIntradayInfo(): IntradayInfo {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return IntradayInfo(
        date = LocalDateTime.parse(timestamp, formatter),
        close = close,
        volume = volume,
        low = low,
        high = high
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun List<IntradayInfoDto>.toDailyIntradayInfos(): List<IntradayInfo> {
    return this.map { it.toIntradayInfo() }
        .filter { it.date.toLocalDate() == LocalDateTime.now().toLocalDate() }
}

@RequiresApi(Build.VERSION_CODES.O)
fun List<IntradayInfoDto>.toWeeklyIntradayInfos(): List<IntradayInfo> {
    val now = LocalDateTime.now()
    val startOfWeek = now.with(java.time.DayOfWeek.MONDAY).toLocalDate()
    val endOfWeek = now.with(java.time.DayOfWeek.FRIDAY).toLocalDate()

    return this.map { it.toIntradayInfo() }
        .filter { it.date.toLocalDate() in startOfWeek..endOfWeek }
}

@RequiresApi(Build.VERSION_CODES.O)
fun List<IntradayInfoDto>.toMonthlyIntradayInfos(): List<IntradayInfo> {
    val currentMonth = LocalDateTime.now().month
    return this.map { it.toIntradayInfo() }
        .filter { it.date.month == currentMonth }
}
