package com.plcoding.stockmarketapp.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.plcoding.stockmarketapp.data.remote.dto.IntradayInfoDto
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Converts an [IntradayInfoDto] to an [IntradayInfo] domain model.
 * Parses the timestamp to a [LocalDateTime] using the given pattern.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun IntradayInfoDto.toIntradayInfo(): IntradayInfo {
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val localDateTime = LocalDateTime.parse(timestamp, formatter)
    return IntradayInfo(
        date = localDateTime,
        close = close,
        volume = volume,
        low = low,
        high = high
    )
}

