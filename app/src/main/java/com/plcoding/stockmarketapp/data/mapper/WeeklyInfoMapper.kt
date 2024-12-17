package com.plcoding.stockmarketapp.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.plcoding.stockmarketapp.data.remote.dto.MonthlyInfoDto
import com.plcoding.stockmarketapp.data.remote.dto.WeeklyInfoDto
import com.plcoding.stockmarketapp.domain.model.MonthlyInfo
import com.plcoding.stockmarketapp.domain.model.WeeklyInfo
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * Converts a [WeeklyInfoDto] data transfer object to a [WeeklyInfo] domain model.
 * Parses the timestamp to a [LocalDate] using the specified date format.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun WeeklyInfoDto.toWeeklyInfo(): WeeklyInfo {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // 调整为匹配实际格式
    return WeeklyInfo(
        date = LocalDate.parse(this.timestamp, formatter),
        open = this.open,
        high = this.high,
        low = this.low,
        close = this.close,
        volume = this.volume
    )
}