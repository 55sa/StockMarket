package com.plcoding.stockmarketapp.data.csv

import android.os.Build
import androidx.annotation.RequiresApi
import com.opencsv.CSVReader
import com.plcoding.stockmarketapp.data.mapper.toIntradayInfo
import com.plcoding.stockmarketapp.data.remote.dto.IntradayInfoDto
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntradayInfoParser @Inject constructor() : CSVParser<IntradayInfo> {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun parse(stream: InputStream): List<IntradayInfo> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return withContext(Dispatchers.IO) {
            csvReader
                .readAll()
                .drop(1) // Skip the header
                .mapNotNull { line ->
                    val timestamp = line.getOrNull(0) ?: return@mapNotNull null
                    val close = line.getOrNull(4)?.toDoubleOrNull() ?: return@mapNotNull null
                    val volume = line.getOrNull(5)?.toDoubleOrNull() ?: 0.0
                    val low = line.getOrNull(3)?.toDoubleOrNull() ?: 0.0
                    val high = line.getOrNull(2)?.toDoubleOrNull() ?: 0.0

                    // Convert to DTO and map to domain model
                    val dto = IntradayInfoDto(timestamp, close, volume, low, high)
                    dto.toIntradayInfo()
                }
                .filter {
                    // Filter by the day you need (e.g., 4 days ago)
                    it.date.toLocalDate() == LocalDate.now().minusDays(4)
                }
                .sortedBy {
                    it.date.hour // Sort by hour
                }
                .also {
                    csvReader.close()
                }
        }
    }
}
