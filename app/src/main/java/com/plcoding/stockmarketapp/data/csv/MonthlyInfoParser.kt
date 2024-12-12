package com.plcoding.stockmarketapp.data.csv

import android.os.Build
import androidx.annotation.RequiresApi
import com.opencsv.CSVReader
import com.plcoding.stockmarketapp.data.mapper.toMonthlyInfo
import com.plcoding.stockmarketapp.data.remote.dto.MonthlyInfoDto
import com.plcoding.stockmarketapp.domain.model.MonthlyInfo
import com.plcoding.stockmarketapp.domain.model.WeeklyInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MonthlyInfoParser @Inject constructor() : CSVParser<MonthlyInfo> {

    /**
     * Parses the input CSV stream and returns a list of [MonthlyInfo] objects.
     * Only the data from the last 12 months are retained.
     *
     * @param stream The input stream of the CSV file.
     * @return A list of [MonthlyInfo] objects representing the last 12 months.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun parse(stream: InputStream): List<MonthlyInfo> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return withContext(Dispatchers.IO) {
            try {
                // Read all lines from the CSV and skip the header row
                val allLines = csvReader.readAll().drop(1)

                // Define the date format matching the CSV's date column
                val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // Adjust pattern as needed

                // Get the current date and calculate the cutoff date (12 months ago)
                val currentDate = LocalDate.now()
                val cutoffDate = currentDate.minusMonths(12)

                allLines
                    .mapNotNull { line ->
                        // Extract each column with safety checks
                        val timestamp = line.getOrNull(0) ?: return@mapNotNull null
                        val open = line.getOrNull(1)?.toDoubleOrNull() ?: return@mapNotNull null
                        val high = line.getOrNull(2)?.toDoubleOrNull() ?: return@mapNotNull null
                        val low = line.getOrNull(3)?.toDoubleOrNull() ?: return@mapNotNull null
                        val close = line.getOrNull(4)?.toDoubleOrNull() ?: return@mapNotNull null
                        val volume = line.getOrNull(5)?.toDoubleOrNull() ?: 0.0

                        // Parse the timestamp to a LocalDate object
                        val date = try {
                            LocalDate.parse(timestamp, dateFormatter)
                        } catch (e: Exception) {
                            // If parsing fails, skip this record
                            return@mapNotNull null
                        }

                        // Filter out records older than 12 months
                        if (date.isBefore(cutoffDate)) {
                            return@mapNotNull null
                        }

                        // Create a DTO and map it to the domain model
                        val dto = MonthlyInfoDto(
                            timestamp = timestamp,
                            open = open,
                            high = high,
                            low = low,
                            close = close,
                            volume = volume
                        )
                        dto.toMonthlyInfo()
                    }
                    .sortedBy { it.date } // Sort the list by date in ascending order
            } finally {

                csvReader.close()
            }
        }
    }
}