package com.plcoding.stockmarketapp.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.plcoding.stockmarketapp.data.remote.dto.MonthlyInfoDto
import com.plcoding.stockmarketapp.domain.model.MonthlyInfo
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun MonthlyInfoDto.toMonthlyInfo(): MonthlyInfo {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // 调整为匹配实际格式
    return MonthlyInfo(
        date = LocalDate.parse(this.timestamp, formatter),
        open = this.open,
        high = this.high,
        low = this.low,
        close = this.close,
        volume = this.volume
    )
}