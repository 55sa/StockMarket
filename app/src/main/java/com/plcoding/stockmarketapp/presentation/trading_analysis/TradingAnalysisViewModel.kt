package com.plcoding.stockmarketapp.presentation.trading_analysis

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import com.plcoding.stockmarketapp.R
import com.plcoding.stockmarketapp.data.csv.TradingDataParser
import com.plcoding.stockmarketapp.domain.model.TradeSide
import com.plcoding.stockmarketapp.domain.model.TradingDataEntry

@HiltViewModel
class TradingAnalysisViewModel  @Inject constructor(
    private val tradingDataParser: TradingDataParser, // 通过 Hilt 注入解析器
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(TradingAnalysisState())
    val state: StateFlow<TradingAnalysisState> = _state

    init {
        // init data
        loadTradingDataFromFile()
    }

    private fun loadTradingDataFromFile() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.resources.openRawResource(R.raw.trading_data)
                val parsedData = tradingDataParser.parse(inputStream)
                _state.emit(_state.value.copy(tradingData = parsedData))
                recalculateAllCharts()
            } catch (e: Exception) {
                Log.e("TradingAnalysis", "Error loading trading data", e)
            }
        }
    }

    fun loadTradingData(tradingData: List<TradingDataEntry>) {
        viewModelScope.launch {
            _state.emit(_state.value.copy(tradingData = tradingData))
            recalculateAllCharts()
        }
    }

    private fun recalculateAllCharts() {
        viewModelScope.launch {
            _state.emit(
                _state.value.copy(
                    dailyVolumeTrend = calculateDailyVolumeTrend(),
                    transactionAmountDistribution = calculateTransactionAmountDistribution(),
                    userActivePeriods = calculateUserActivePeriods(),
                    userCategoryPreferences = calculateUserCategoryPreferences(),
                    monthlyTransactionAnalysis = calculateMonthlyTransactionAnalysis(),
                    profitLossDistribution = calculateProfitLossDistribution()
                )
            )
        }
    }


    // Toggle chart visibility
    fun toggleChart(chart: (TradingAnalysisState) -> TradingAnalysisState) {
        viewModelScope.launch {
            _state.value = chart(_state.value)
        }
    }

    // Add methods for processing tradingData and returning chart data
    private fun calculateDailyVolumeTrend(): List<Pair<String, Double>> {
        Log.d("testing", "_state.value.tradingData: ${_state.value.tradingData}")
        val dailyVT = _state.value.tradingData.groupBy { it.createdAt.substring(0, 10) } // Group by date
            .map { (date, entries) ->
                date to entries.sumOf { it.filledAssetQuantity }
            }
        Log.d("testing", "calculateDailyVolumeTrend: ${dailyVT.size}")
        return dailyVT
    }

    private fun calculateTransactionAmountDistribution(): Map<TradeSide, Double> {
        return _state.value.tradingData.groupBy { it.side } // Group by TradeSide (BUY/SELL)
            .mapValues { (_, entries) ->
                entries.sumOf { entry ->
                    entry.averagePrice * entry.filledAssetQuantity
                }
            }
    }

    private fun calculateUserActivePeriods(): List<Pair<String, Double>> {
        return _state.value.tradingData.groupBy { it.createdAt.substring(11, 13) } // Group by hour
            .map { (hour, entries) -> hour to entries.size.toDouble() }
            .sortedBy { it.first }
    }

    private fun calculateUserCategoryPreferences(): Map<String, Double> {
        return _state.value.tradingData.groupBy { it.symbol }
            .mapValues { (_, entries) -> entries.size.toDouble()  }
    }

    private fun calculateMonthlyTransactionAnalysis(): Map<String, Double> {
        return _state.value.tradingData.groupBy { it.createdAt.substring(0, 7) } // Group by year-month
            .mapValues { (_, entries) -> entries.size.toDouble()  }
    }

    private fun calculateProfitLossDistribution(): Map<TradeSide, Double> {
        return _state.value.tradingData.groupBy { it.side }
            .mapValues { (side, entries) ->
                entries.sumOf { entry ->
                    val profitLoss = entry.averagePrice * entry.filledAssetQuantity
                    if (side == TradeSide.BUY) -profitLoss else profitLoss
                }
            }
    }
}