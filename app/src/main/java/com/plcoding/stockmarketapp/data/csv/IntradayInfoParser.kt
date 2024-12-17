package com.plcoding.stockmarketapp.data.csv

import android.os.Build
import androidx.annotation.RequiresApi
import com.opencsv.CSVReader
import com.plcoding.stockmarketapp.data.mapper.toIntradayInfo
import com.plcoding.stockmarketapp.data.remote.dto.IntradayInfoDto
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

    /**
     * Parses the input CSV stream and returns a list of [IntradayInfo] objects.
     * Filters the data for the current trading day and sorts it by hour.
     *
     * @param stream The input stream of the CSV file.
     * @return A list of [IntradayInfo] objects representing intraday data.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun parse(stream: InputStream): List<IntradayInfo> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return withContext(Dispatchers.IO) {
            // Determine the effective trading day for filtering
            val tradingDay = getEffectiveTradingDay()
            csvReader
                .readAll()
                .drop(1) // Skip the header row
                .mapNotNull { line ->
                    // Safely extract and convert required fields
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
                    // Filter records to only include data from the determined trading day
                    it.date.toLocalDate() == tradingDay
                }
                .sortedBy {
                    // Sort the data by hour
                    it.date.hour
                }
                .also {
                    // Close the CSV reader to free up resources
                    csvReader.close()
                }
        }
    }

    /**
     * Determine the effective trading day based on the current time.
     * If after 9 PM on a trading day, use today's date. Otherwise, use the most recent trading day.
     *
     * @return The effective trading day as a [LocalDate].
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getEffectiveTradingDay(): LocalDate {
        val now = LocalDateTime.now()
        val ninePM = now.toLocalDate().atTime(21, 0) // 9 PM cutoff time

        return if (now.isAfter(ninePM) && isTradingDay(LocalDate.now())) {
            // If current time is after 9 PM on a trading day, return today's date
            LocalDate.now()
        } else {
            // Otherwise, find the most recent trading day
            getMostRecentTradingDay(LocalDate.now().minusDays(1))
        }
    }

    /**
     * Find the most recent trading day, skipping weekends and holidays.
     *
     * @param date The date to start checking from.
     * @return The most recent trading day as a [LocalDate].
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMostRecentTradingDay(date: LocalDate): LocalDate {
        var currentDay = date
        while (!isTradingDay(currentDay)) {
            // Move backward until a trading day is found
            currentDay = currentDay.minusDays(1)
        }
        return currentDay
    }

    /**
     * Check if a given date is a trading day.
     * Trading days exclude weekends; holidays can be added as needed.
     *
     * @param date The date to check.
     * @return True if the date is a trading day, false otherwise.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun isTradingDay(date: LocalDate): Boolean {
        return when (date.dayOfWeek) {
            java.time.DayOfWeek.SATURDAY, java.time.DayOfWeek.SUNDAY -> false
            else -> true // Additional holiday checks can be added here
        }
    }
}
