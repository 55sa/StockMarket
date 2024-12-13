package com.plcoding.stockmarketapp.presentation.trading_analysis

import android.content.Context
import android.util.Log
import java.time.format.DateTimeFormatter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import com.plcoding.stockmarketapp.R
import com.plcoding.stockmarketapp.data.csv.NasdaqScreenerParser
import com.plcoding.stockmarketapp.data.csv.TradingDataParser
import com.plcoding.stockmarketapp.domain.model.TradeSide
import com.plcoding.stockmarketapp.domain.model.TradingDataEntry

private val DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME


@HiltViewModel
class TradingAnalysisViewModel  @Inject constructor(
    private val tradingDataParser: TradingDataParser, // 通过 Hilt 注入解析器
    private val nasdaqScreenerParser: NasdaqScreenerParser,

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

                val inputStreamNasdaq = context.resources.openRawResource(R.raw.nasdaq_screener)
                val nasdaqStockData = nasdaqScreenerParser.parse(inputStreamNasdaq)
                _state.emit(_state.value.copy(nasdaqCompanyData = nasdaqStockData))

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

                    //
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
    private fun calculateDailyVolumeTrend(): Map<String, Double> {
//        Log.d("testing", "_state.value.tradingData: ${_state.value.tradingData}")
        val dailyVT = _state.value.tradingData.groupBy { it.createdAt.substring(0, 10) } // Group by date
            .mapValues { (_, entries) ->
                entries.sumOf { it.filledAssetQuantity }
            }
//        Log.d("testing", "calculateDailyVolumeTrend: ${dailyVT.size}")
        return dailyVT
    }

    private fun calculateTransactionAmountDistribution(): Map<String, Double> {
        return _state.value.tradingData.groupBy { it.side } // Group by TradeSide (BUY/SELL)
            .mapKeys { (side, _) -> side.toString() } // 将 TradeSide 转换为 String
            .mapValues { (_, entries) ->
                entries.sumOf { entry ->
                    entry.averagePrice * entry.filledAssetQuantity
                }
            }
    }


    private fun calculateUserActivePeriods(): Map<String, Double> {
        return _state.value.tradingData
            .groupBy { it.createdAt.substring(11, 16) } // 提取时间部分 (HH:mm)
            .mapKeys { (time, _) ->
                val hourMinute = time.split(":").map { it.toInt() }
                val hour = hourMinute[0]
                val minute = hourMinute[1]

                when {
                    hour < 9 || (hour == 9 && minute < 30) -> "Pre-Market" // 早于9:30
                    hour >= 16 -> "Post-Market" // 下午4点及以后
                    else -> {
                        // 根据小时划分普通交易时间段
                        val startHour = if (minute < 30) "$hour:00" else "$hour:30"
                        startHour
                    }
                }
            }
            .mapValues { (_, entries) ->
                entries.sumOf { it.filledAssetQuantity } // 对分组内的 filledAssetQuantity 求和
            }
            .toSortedMap(compareBy {
                when (it) {
                    "Pre-Market" -> 0
                    "Post-Market" -> Int.MAX_VALUE
                    else -> it.substringBefore(":").toInt() // 按小时排序
                }
            })
    }


    private fun calculateUserCategoryPreferences(): Map<String, Double> {
        val nasdaqIndustryMap = _state.value.nasdaqCompanyData.associateBy { it.symbol }

        return _state.value.tradingData.groupBy { it.symbol }
            .mapNotNull { (symbol, entries) ->
                val industry = nasdaqIndustryMap[symbol]?.industry ?: return@mapNotNull null
                industry to entries.size.toDouble()
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, sizes) -> sizes.sum() }
    }

    private fun calculateMonthlyTransactionAnalysis(): Map<String, Double> {
        return _state.value.tradingData.groupBy { it.createdAt.substring(0, 7) } // Group by year-month
            .mapValues { (_, entries) -> entries.size.toDouble()  }
    }

    private fun calculateProfitLossDistribution(): Map<String, Double> {
        return _state.value.tradingData.groupBy { it.side }
            .mapKeys { (side, _) -> side.toString() }
            .mapValues { (_, entries) ->
                entries.sumOf { entry ->
                    val profitLoss = entry.averagePrice * entry.filledAssetQuantity
                    if (entry.side == TradeSide.BUY) -profitLoss else profitLoss
                }
            }
    }

//    private fun calculateWeeklySummary(tradingData: List<TradingDataEntry>): Map<String, Any> {
//        // 1. 获取当前时间和一周前的时间
//        val now = LocalDateTime.now()
//        val oneWeekAgo = now.minusWeeks(1)
//
//        // 2. 筛选出上一周的交易记录
//        val lastWeekTrades = tradingData.filter {
//            val tradeTime = LocalDateTime.parse(it.createdAt, DATE_FORMATTER)
//            tradeTime.isAfter(oneWeekAgo) && tradeTime.isBefore(now)
//        }
//
//        // 3. 初始化结果变量
//        var netProfitLoss = 0.0
//        val stockTradeCount = mutableMapOf<String, Int>()
//        val tradeResults = mutableListOf<Map<String, Float>>()
//
//        // 4. 遍历每条交易记录
//        for (trade in lastWeekTrades) {
//            // 4.1 计算当前交易的盈亏
//            val executionDetails = parseJsonString(trade.executions) // 假设这个是一个解析JSON的方法
//            val effectivePrice = executionDetails["effective_price"]?.toDouble() ?: 0.0
//            val quantity = executionDetails["quantity"]?.toDouble() ?: 0.0
//            val profitLoss = if (trade.side == TradeSide.BUY) {
//                -effectivePrice * quantity // 买入为负值
//            } else {
//                effectivePrice * quantity // 卖出为正值
//            }
//            netProfitLoss += profitLoss
//
//            // 4.2 统计每只股票的操作次数
//            stockTradeCount[trade.symbol] = stockTradeCount.getOrDefault(trade.symbol, 0) + 1
//
//            // 4.3 记录每次操作的结果
//            if (trade.filledAssetQuantity == 0.0 && trade.side == TradeSide.SELL) { // 假设 filledAssetQuantity = 0 表示清仓
//                tradeResults[trade.symbol] = (tradeResults[trade.symbol] ?: 0f) + profitLoss.toFloat()
//            }
//        }
//
//        // 5. 返回汇总结果
//        return mapOf(
//            "netProfitLoss" to netProfitLoss,
//            "stockTradeCount" to stockTradeCount,
//            "tradeResults" to tradeResults
//        )
//    }


}