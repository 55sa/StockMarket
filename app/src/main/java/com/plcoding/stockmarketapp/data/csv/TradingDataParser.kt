package com.plcoding.stockmarketapp.data.csv

import com.opencsv.CSVReader
import com.plcoding.stockmarketapp.domain.model.OrderState
import com.plcoding.stockmarketapp.domain.model.OrderType
import com.plcoding.stockmarketapp.domain.model.TradeSide
import com.plcoding.stockmarketapp.domain.model.TradingDataEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TradingDataParser @Inject constructor() : CSVParser<TradingDataEntry> {

    /**
     * Parses the input CSV stream and returns a list of [TradingDataEntry] objects.
     * Handles safe extraction of fields and converts them into a domain model.
     *
     * @param stream The input stream of the CSV file.
     * @return A list of [TradingDataEntry] objects representing trading data.
     */
    override suspend fun parse(stream: InputStream): List<TradingDataEntry> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return withContext(Dispatchers.IO) {
            csvReader
                .readAll()
                .drop(1) // Skip header row
                .mapNotNull { line ->
                    try {
                        // Safely parse each field
                        val id = line.getOrNull(0) ?: return@mapNotNull null
                        val accountNumber = line.getOrNull(1) ?: return@mapNotNull null
                        val symbol = line.getOrNull(2) ?: return@mapNotNull null
                        val side = parseTradeSide(line.getOrNull(4)) ?: return@mapNotNull null
                        val executions = line.getOrNull(5) ?: return@mapNotNull null
                        val type = parseOrderType(line.getOrNull(6)) ?: return@mapNotNull null
                        val state = parseOrderState(line.getOrNull(7)) ?: return@mapNotNull null
                        val averagePrice = line.getOrNull(8)?.toDoubleOrNull() ?: return@mapNotNull null
                        val filledAssetQuantity = line.getOrNull(9)?.toDoubleOrNull() ?: return@mapNotNull null
                        val createdAt = line.getOrNull(10) ?: return@mapNotNull null
                        val updatedAt = line.getOrNull(11) ?: return@mapNotNull null
                        val marketOrderConfig = line.getOrNull(12) ?: ""
                        val limitOrderConfig = line.getOrNull(13) ?: ""
                        val stopLossOrderConfig = line.getOrNull(14) ?: ""
                        val stopLimitOrderConfig = line.getOrNull(15) ?: ""

                        // Build the domain model
                        TradingDataEntry(
                            id = id,
                            accountNumber = accountNumber,
                            symbol = symbol,
                            side = side,
                            executions = parseJson(executions),
                            type = type,
                            state = state,
                            averagePrice = averagePrice,
                            filledAssetQuantity = filledAssetQuantity,
                            createdAt = createdAt,
                            updatedAt = updatedAt,
                            market_order_config = parseJson(marketOrderConfig),
                            limit_order_config = parseJson(limitOrderConfig),
                            stop_loss_order_config = parseJson(stopLossOrderConfig),
                            stop_limit_order_config = parseJson(stopLimitOrderConfig)
                        )
                    } catch (e: Exception) {
                        // Return null for any parsing issues
                        null
                    }
                }
                .also {
                    // Close the CSV reader to release resources
                    csvReader.close()
                }
        }
    }

    /**
     * Parses the trade side from a string.
     *
     * @param side The string representation of the trade side.
     * @return The corresponding [TradeSide] enum or null if invalid.
     */
    private fun parseTradeSide(side: String?): TradeSide? {
        return when (side?.uppercase()) {
            "BUY" -> TradeSide.BUY
            "SELL" -> TradeSide.SELL
            else -> null
        }
    }

    /**
     * Parses the order type from a string.
     *
     * @param type The string representation of the order type.
     * @return The corresponding [OrderType] enum or null if invalid.
     */
    private fun parseOrderType(type: String?): OrderType? {
        return when (type?.uppercase()) {
            "MARKET" -> OrderType.MARKET
            "LIMIT" -> OrderType.LIMIT
            "STOP_LIMIT" -> OrderType.STOP_LIMIT
            "STOP_LOSS" -> OrderType.STOP_LOSS
            else -> null
        }
    }

    /**
     * Parses the order state from a string.
     *
     * @param state The string representation of the order state.
     * @return The corresponding [OrderState] enum or null if invalid.
     */
    private fun parseOrderState(state: String?): OrderState? {
        return when (state?.uppercase()) {
            "OPEN" -> OrderState.OPEN
            "CLOSED" -> OrderState.CLOSED
            "CANCELLED" -> OrderState.CANCELLED
            else -> null
        }
    }

    /**
     * Parses a JSON-like string by replacing single quotes with double quotes.
     *
     * @param jsonString The input string.
     * @return A valid JSON string.
     */
    private fun parseJson(jsonString: String): String {
        return try {
            jsonString.replace("'", "\"")
        } catch (e: Exception) {
            ""
        }
    }
}
