package com.plcoding.stockmarketapp.data.csv

import com.opencsv.CSVReader
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompanyListingsParser @Inject constructor(): CSVParser<CompanyListing> {

    /**
     * Parses the input CSV stream and returns a list of [CompanyListing] objects.
     * Safely extracts and maps each record from the CSV file.
     *
     * @param stream The input stream of the CSV file.
     * @return A list of [CompanyListing] objects containing company details.
     */
    override suspend fun parse(stream: InputStream): List<CompanyListing> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return withContext(Dispatchers.IO) {
            csvReader
                .readAll()
                .drop(1) // Skip header row
                .mapNotNull { line ->
                    // Safely parse each column and create a CompanyListing object
                    val symbol = line.getOrNull(0)
                    val name = line.getOrNull(1)
                    val exchange = line.getOrNull(2)

                    // Return null if any required field is missing
                    CompanyListing(
                        name = name ?: return@mapNotNull null,
                        symbol = symbol ?: return@mapNotNull null,
                        exchange = exchange ?: return@mapNotNull null
                    )
                }
                .also {
                    // Close the CSV reader to release resources
                    csvReader.close()
                }
        }
    }
}
