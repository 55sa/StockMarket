package com.plcoding.stockmarketapp.domain.model


enum class TradeSide {
    BUY,
    SELL
}
enum class OrderState {
    OPEN,
    CLOSED,
    CANCELLED
}

enum class OrderType {
    MARKET,
    LIMIT,
    STOP_LIMIT,
    STOP_LOSS,
}

data class TradingDataEntry(
    val id: String,
    val accountNumber: String,
    val symbol: String,
    val side: TradeSide,
    val executions: String,
    val type: OrderType,
    val state: OrderState,
    val averagePrice: Double,
    val filledAssetQuantity: Double,
    val createdAt: String,
    val updatedAt: String,
    val market_order_config: String,
    val limit_order_config: String,
    val stop_loss_order_config: String,
    val stop_limit_order_config: String
)