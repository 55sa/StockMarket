
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
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntradayInfoParser @Inject constructor() : CSVParser<IntradayInfo> {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun parse(stream: InputStream): List<IntradayInfo> {
        return parseCsv(stream) { getEffectiveTradingDay() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun parseWeekly(stream: InputStream): List<IntradayInfo> {
        return parseCsv(stream) { getEffectiveTradingWeek() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun parseMonthly(stream: InputStream): List<IntradayInfo> {
        return parseCsv(stream) { getEffectiveTradingMonth() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun parseCsv(
        stream: InputStream,
        dateProvider: () -> Any
    ): List<IntradayInfo> {
        val csvReader = CSVReader(InputStreamReader(stream))
        val dateRange = dateProvider()

        return withContext(Dispatchers.IO) {
            csvReader.use {
                csvReader.readAll()
                    .drop(1) // Skip the header
                    .mapNotNull { line ->
                        val timestamp = line.getOrNull(0) ?: return@mapNotNull null
                        val close = line.getOrNull(4)?.toDoubleOrNull() ?: return@mapNotNull null
                        val volume = line.getOrNull(5)?.toDoubleOrNull() ?: 0.0
                        val low = line.getOrNull(3)?.toDoubleOrNull() ?: 0.0
                        val high = line.getOrNull(2)?.toDoubleOrNull() ?: 0.0

                        IntradayInfoDto(timestamp, close, volume, low, high).toIntradayInfo()
                    }
                    .filter { intradayInfo ->
                        when (dateRange) {
                            is LocalDate -> intradayInfo.date.toLocalDate() == dateRange
                            is Pair<*, *> -> {
                                val start = dateRange.first as LocalDate
                                val end = dateRange.second as LocalDate
                                val current = intradayInfo.date.toLocalDate()
                                current >= start && current <= end
                            }
                            else -> true
                        }
                    }
                    .sortedBy { it.date.hour }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMostRecentTradingDay(date: LocalDate): LocalDate {
        var currentDay = date
        while (!isTradingDay(currentDay)) {
            currentDay = currentDay.minusDays(1)
        }
        return currentDay
    }

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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getEffectiveTradingWeek(): Pair<LocalDate, LocalDate> {
        val now = LocalDate.now()
        val startOfWeek = now.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
        val endOfWeek = now.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.FRIDAY))
        return startOfWeek to endOfWeek
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getEffectiveTradingMonth(): LocalDate {
        return LocalDate.now().withDayOfMonth(1)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun isTradingDay(date: LocalDate): Boolean {
        return when (date.dayOfWeek) {
            java.time.DayOfWeek.SATURDAY, java.time.DayOfWeek.SUNDAY -> false
            else -> true // Add additional checks for holidays here if needed
        }
    }
}
