package com.plcoding.stockmarketapp.data.csv

import android.os.Build
import androidx.annotation.RequiresApi
import com.opencsv.CSVReader
import com.plcoding.stockmarketapp.data.mapper.toMonthlyInfo
import com.plcoding.stockmarketapp.data.mapper.toWeeklyInfo
import com.plcoding.stockmarketapp.data.remote.dto.MonthlyInfoDto
import com.plcoding.stockmarketapp.data.remote.dto.WeeklyInfoDto
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import com.plcoding.stockmarketapp.domain.model.MonthlyInfo
import com.plcoding.stockmarketapp.domain.model.WeeklyInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeeklyInfoParser @Inject constructor() : CSVParser<WeeklyInfo>{


    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun parse(stream: InputStream): List<WeeklyInfo> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return withContext(Dispatchers.IO) {

            csvReader
                .readAll()
                .drop(1) // Skip the header
                .mapNotNull { line ->
                    val timestamp = line.getOrNull(0) ?: return@mapNotNull null
                    val open = line.getOrNull(1)?.toDoubleOrNull() ?: return@mapNotNull null
                    val high = line.getOrNull(2)?.toDoubleOrNull() ?: return@mapNotNull null
                    val low = line.getOrNull(3)?.toDoubleOrNull() ?: return@mapNotNull null
                    val close = line.getOrNull(4)?.toDoubleOrNull() ?: return@mapNotNull null
                    val volume = line.getOrNull(5)?.toDoubleOrNull() ?: 0.0

                    // Convert to DTO and map to domain model
                    val dto = WeeklyInfoDto(
                        timestamp = timestamp,
                        open = open,
                        high = high,
                        low = low,
                        close = close,
                        volume = volume
                    )
                    dto.toWeeklyInfo()
                }
                .sortedBy {
                    it.date
                }
                .also {
                    csvReader.close()
                }
        }
    }
}