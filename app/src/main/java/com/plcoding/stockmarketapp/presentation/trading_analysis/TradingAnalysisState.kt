package com.plcoding.stockmarketapp.presentation.trading_analysis

import com.plcoding.stockmarketapp.domain.model.NasdaqCompanyScreener
import com.plcoding.stockmarketapp.domain.model.TradingDataEntry

data class TradingAnalysisState (
    var tradingData: List<TradingDataEntry>  = emptyList(),
    var nasdaqCompanyData: List<NasdaqCompanyScreener> = emptyList(),

    val showDailyVolumeTrend: Boolean = true,
    val showTransactionAmountDistribution: Boolean = false,
    val showUserActivePeriods: Boolean = false,
    val showUserCategoryPreferences: Boolean = false,
    val showMonthlyTransactionAnalysis: Boolean = false,
    val showProfitLossDistribution: Boolean = false,


    // for charts
    val dailyVolumeTrend: Map<String, Double> = emptyMap(),
    val transactionAmountDistribution: Map<String, Double> = emptyMap(),
    val userActivePeriods: Map<String, Double> = emptyMap(),
    val userCategoryPreferences: Map<String, Double> = emptyMap(),
    val monthlyTransactionAnalysis: Map<String, Double> = emptyMap(),
    val profitLossDistribution: Map<String, Double> = emptyMap(),


    val totalTrades: Int = 0,
    val tradeGrowthPercentage: Double = 0.0,
    val totalTTrades: Int = 0,
    val successfulTradePercentage: Double = 0.0,
    val totalStocksTraded: Int = 0,
    val stocksCurrentlyHeld: Int = 0,
    val stocksCleared: Int = 0,
    val mostTradedStock: String = "",
    val mostActiveSector: String = "",
    val mostActiveBuyTime: String = "",
    val mostActiveBuyCount: Int = 0,
    val mostActiveSellTime: String = "",
    val mostActiveSellCount: Int = 0
)