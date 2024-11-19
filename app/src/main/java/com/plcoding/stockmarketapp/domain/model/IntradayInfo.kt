package com.plcoding.stockmarketapp.domain.model

import android.os.storage.StorageVolume
import java.time.LocalDateTime

data class IntradayInfo(
    val date: LocalDateTime,
    val close: Double,
    val volume: Double,
    val low: Double,
    val high: Double
)
