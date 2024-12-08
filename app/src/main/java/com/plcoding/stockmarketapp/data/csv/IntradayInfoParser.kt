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
            val tradingDay = getEffectiveTradingDay()
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
                    // Ensure the data belongs to the determined trading day
                    it.date.toLocalDate() == tradingDay
                }
                .sortedBy {
                    it.date.hour // Sort by hour
                }
                .also {
                    csvReader.close()
                }
        }
    }

    /**
     * Determine the effective trading day based on the current time.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getEffectiveTradingDay(): LocalDate {
        val now = LocalDateTime.now()
        val ninePM = now.toLocalDate().atTime(21, 0) // Today's 9 PM

        return if (now.isAfter(ninePM) && isTradingDay(LocalDate.now())) {
            // If after 9 PM on a trading day, use today's date
            LocalDate.now()
        } else {
            // Otherwise, find the most recent trading day
            getMostRecentTradingDay(LocalDate.now().minusDays(1))
        }
    }

    /**
     * Get the most recent trading day by skipping weekends and holidays.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMostRecentTradingDay(date: LocalDate): LocalDate {
        var currentDay = date
        while (!isTradingDay(currentDay)) {
            currentDay = currentDay.minusDays(1)
        }
        return currentDay
    }

    /**
     * Check if a given date is a trading day.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun isTradingDay(date: LocalDate): Boolean {
        return when (date.dayOfWeek) {
            java.time.DayOfWeek.SATURDAY, java.time.DayOfWeek.SUNDAY -> false
            else -> true // Add additional checks for holidays here if needed
        }
    }
}
