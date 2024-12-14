package com.plcoding.stockmarketapp.presentation.trading_analysis

import com.plcoding.stockmarketapp.domain.model.NasdaqCompanyScreener
import com.plcoding.stockmarketapp.domain.model.TradingDataEntry

data class TradingAnalysisState (
    var tradingData: List<TradingDataEntry>  = emptyList(),
    var nasdaqCompanyData: List<NasdaqCompanyScreener> = emptyList(),

    // for charts V 1.0
    val dailyVolumeTrend: Map<String, Double> = emptyMap(),
    val transactionAmountDistribution: Map<String, Double> = emptyMap(),
    val userActivePeriods: Map<String, Double> = emptyMap(),
    val userCategoryPreferences: Map<String, Double> = emptyMap(),
    val weeklyTransactionAnalysis:  Map<String, Pair<Double,Double>> = emptyMap(),


    // V 2.0

    // for Weekly Analysis
    var lastWeekData: List<TradingDataEntry> = emptyList(),
    var weekBeforeLastData: List<TradingDataEntry> = emptyList(),

    // for Weekly User Behavior Analysis
    val weeklyTotalTrades: Int = 0,
    val weeklyTradeGrowthPercentage: Double = 0.0,
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
    val mostActiveSellCount: Int = 0,
    val weeklyTransactionChange: Double = 0.0,

    val companyPreferences: Map<String, Double> = emptyMap(),

    // for Profit Analysis
    var clearings: List<ClearingInfo> = emptyList(),
    val companyWinRate: Map<String,Double> = emptyMap(),

    // Previous week's data for comparison
    val lastWeekCompanyWinRate: Map<String,Double> = emptyMap(),
    val lastWeekClearings: List<ClearingInfo> = emptyList(),

)

data class ClearingInfo(
    val symbol: String,
    val netProfit: Double,
    val profitPercentage: Double,
    val successCount: Int,
    val failureCount: Int,
    val holdings: Map<String, Pair<Double, Double>>,
    val clearedCount: Int,
)