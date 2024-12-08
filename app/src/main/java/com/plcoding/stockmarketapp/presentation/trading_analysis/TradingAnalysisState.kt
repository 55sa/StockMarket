package com.plcoding.stockmarketapp.presentation.trading_analysis

import com.plcoding.stockmarketapp.domain.model.TradeSide
import com.plcoding.stockmarketapp.domain.model.TradingDataEntry

data class TradingAnalysisState (
    var tradingData: List<TradingDataEntry>  = emptyList(),
    val showDailyVolumeTrend: Boolean = false,
    val showTransactionAmountDistribution: Boolean = false,
    val showUserActivePeriods: Boolean = false,
    val showUserCategoryPreferences: Boolean = false,
    val showMonthlyTransactionAnalysis: Boolean = false,
    val showProfitLossDistribution: Boolean = false,


    // for charts
    val dailyVolumeTrend: List<Pair<String, Double>> = emptyList(),
    val transactionAmountDistribution: Map<TradeSide, Double> = emptyMap(),
    val userActivePeriods: List<Pair<String, Double>> = emptyList(),
    val userCategoryPreferences: Map<String, Double> = emptyMap(),
    val monthlyTransactionAnalysis: Map<String, Double> = emptyMap(),
    val profitLossDistribution: Map<TradeSide, Double> = emptyMap()
)