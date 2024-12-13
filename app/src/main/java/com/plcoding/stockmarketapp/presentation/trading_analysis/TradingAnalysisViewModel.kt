package com.plcoding.stockmarketapp.presentation.trading_analysis

import android.annotation.SuppressLint
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
import com.plcoding.stockmarketapp.domain.model.OrderState
import com.plcoding.stockmarketapp.domain.model.OrderType
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
                    // V 1.0
                    dailyVolumeTrend = calculateDailyVolumeTrend(),
                    transactionAmountDistribution = calculateTransactionAmountDistribution(),
                    userActivePeriods = calculateUserActivePeriods(),
                    userCategoryPreferences = calculateUserCategoryPreferences(),
                    monthlyTransactionAnalysis = calculateMonthlyTransactionAnalysis(),
                    profitLossDistribution = calculateProfitLossDistribution(),

                    // V 2.0
                    totalTrades = calculateTotalTrades(),
                    tradeGrowthPercentage = calculateTradeGrowth(),
                    totalTTrades = calculateTotalTTrades(),
                    successfulTradePercentage = calculateSuccessfulTradePercentage(),
                    totalStocksTraded = calculateTotalStocksTraded(),
                    stocksCurrentlyHeld = calculateStocksCurrentlyHeld(),
                    stocksCleared = calculateStocksCleared(),
                    mostTradedStock = calculateMostTradedStock(),
                    mostActiveSector = calculateMostActiveSector(),
                    mostActiveBuyTime = calculateMostActiveBuyTime(),
                    mostActiveBuyCount = calculateMostActiveBuyCount(),
                    mostActiveSellTime = calculateMostActiveSellTime(),
                    mostActiveSellCount = calculateMostActiveSellCount()

                )
            )
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

    // V 2.0

    // 总交易次数
    private fun calculateTotalTrades(): Int {
        return _state.value.tradingData.size
    }

    // 交易增长百分比
    private fun calculateTradeGrowth(): Double {
        val lastWeekData = getLastWeekData()
        val currentTrades = _state.value.tradingData.size
        val lastWeekTrades = lastWeekData.size
        return if (lastWeekTrades > 0) {
            ((currentTrades - lastWeekTrades).toDouble() / lastWeekTrades) * 100
        } else 0.0
    }

    // 做 T 的交易次数
    private fun calculateTotalTTrades(): Int {
        return _state.value.tradingData.count { it.type == OrderType.STOP_LIMIT || it.type == OrderType.STOP_LOSS }
    }

    // 做 T 的成功率
    @SuppressLint("DefaultLocale")
    private fun calculateSuccessfulTradePercentage(): Double {
        val tTrades = _state.value.tradingData.filter { it.type == OrderType.STOP_LIMIT || it.type == OrderType.STOP_LOSS }
        val successfulTrades = tTrades.count { it.state == OrderState.CLOSED }
        return if (tTrades.isNotEmpty()) {
            String.format("%.2f", (successfulTrades.toDouble() / tTrades.size) * 100).toDouble()
        } else 0.0
    }

    // 交易的股票总数
    private fun calculateTotalStocksTraded(): Int {
        return _state.value.tradingData.map { it.symbol }.distinct().size
    }

    // 当前持仓的股票数量
    private fun calculateStocksCurrentlyHeld(): Int {
        return _state.value.tradingData.filter { it.state != OrderState.CLOSED }.map { it.symbol }.distinct().size
    }

    // 已清仓的股票数量
    private fun calculateStocksCleared(): Int {
        val totalStocks = _state.value.tradingData.map { it.symbol }.distinct().size
        val heldStocks = _state.value.tradingData.filter { it.state != OrderState.CLOSED }.map { it.symbol }.distinct().size
        return totalStocks - heldStocks
    }

    // 交易次数最多的股票
    private fun calculateMostTradedStock(): String {
        return _state.value.tradingData.groupingBy { it.symbol }.eachCount().maxByOrNull { it.value }?.key.orEmpty()
    }

    // 最活跃的行业
    private fun calculateMostActiveSector(): String {
        val nasdaqIndustryMap = _state.value.nasdaqCompanyData.associateBy { it.symbol }
        return _state.value.tradingData.mapNotNull { entry ->
            nasdaqIndustryMap[entry.symbol]?.industry
        }.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key.orEmpty()
    }

    // 最活跃的买入时间段
    private fun calculateMostActiveBuyTime(): String {
        return _state.value.tradingData.filter { it.side == TradeSide.BUY }
            .groupBy { it.createdAt.substring(11, 13) } // 按小时分组
            .maxByOrNull { (_, entries) -> entries.size }
            ?.key?.let { "$it:00-$it:59" }.orEmpty()
    }

    // 最活跃的买入交易数量
    private fun calculateMostActiveBuyCount(): Int {
        val buyTimes = _state.value.tradingData.filter { it.side == TradeSide.BUY }
            .groupingBy { it.createdAt.substring(11, 13) } // 按小时分组
            .eachCount()
        val mostActiveTime = buyTimes.maxByOrNull { it.value }?.key
        return buyTimes[mostActiveTime] ?: 0
    }

    // 最活跃的卖出时间段
    private fun calculateMostActiveSellTime(): String {
        return _state.value.tradingData.filter { it.side == TradeSide.SELL }
            .groupBy { it.createdAt.substring(11, 13) } // 按小时分组
            .maxByOrNull { (_, entries) -> entries.size }
            ?.key?.let { "$it:00-$it:59" }.orEmpty()
    }

    // 最活跃的卖出交易数量
    private fun calculateMostActiveSellCount(): Int {
        val sellTimes = _state.value.tradingData.filter { it.side == TradeSide.SELL }
            .groupingBy { it.createdAt.substring(11, 13) } // 按小时分组
            .eachCount()
        val mostActiveTime = sellTimes.maxByOrNull { it.value }?.key
        return sellTimes[mostActiveTime] ?: 0
    }

    // 获取上周交易数据
    private fun getLastWeekData(): List<TradingDataEntry> {
        return _state.value.tradingData.filter { it.createdAt.substring(0, 10) in getLastWeekRange() }
    }

    // 获取上周时间范围
    private fun getLastWeekRange(): List<String> {
        val now = java.time.LocalDate.now()
        val lastWeekDates = (1..7).map { now.minusDays(it.toLong()).toString() }
        return lastWeekDates
    }


}