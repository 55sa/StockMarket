package com.plcoding.stockmarketapp.data.csv

import com.opencsv.CSVReader
import com.plcoding.stockmarketapp.domain.model.NasdaqCompanyScreener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NasdaqScreenerParser @Inject constructor() : CSVParser<NasdaqCompanyScreener> {

    override suspend fun parse(stream: InputStream): List<NasdaqCompanyScreener> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return withContext(Dispatchers.IO) {
            csvReader
                .readAll()
                .drop(1) // Skip the header row
                .mapNotNull { line ->
                    val symbol = line.getOrNull(0)
                    val name = line.getOrNull(1)
                    val lastSale = line.getOrNull(2)?.removePrefix("$")?.toDoubleOrNull()
                    val netChange = line.getOrNull(3)?.toDoubleOrNull()
                    val percentChange = line.getOrNull(4)?.removeSuffix("%")?.toDoubleOrNull()
                    val marketCap = line.getOrNull(5)?.toDoubleOrNull()
                    val country = line.getOrNull(6)
                    val ipoYear = line.getOrNull(7)?.toIntOrNull()
                    val volume = line.getOrNull(8)?.toIntOrNull()
                    val sector = line.getOrNull(9)
                    val industry = line.getOrNull(10)

                    NasdaqCompanyScreener(
                        symbol = symbol ?: return@mapNotNull null,
                        name = name ?: return@mapNotNull null,
                        lastSale = lastSale ?: return@mapNotNull null,
                        netChange = netChange ?: return@mapNotNull null,
                        percentChange = percentChange ?: return@mapNotNull null,
                        marketCap = marketCap ?: return@mapNotNull null,
                        country = country ?: return@mapNotNull null,
                        ipoYear = ipoYear ?: return@mapNotNull null,
                        volume = volume ?: return@mapNotNull null,
                        sector = sector ?: return@mapNotNull null,
                        industry = industry ?: return@mapNotNull null
                    )
                }
                .also {
                    csvReader.close()
                }
        }
    }
}