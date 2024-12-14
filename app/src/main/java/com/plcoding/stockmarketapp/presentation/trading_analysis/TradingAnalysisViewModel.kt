package com.plcoding.stockmarketapp.presentation.trading_analysis

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import java.time.format.DateTimeFormatter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
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

    val referenceDate = java.time.LocalDate.of(2024, 11, 22) // Dummy Data TODO

    init {
        // init data
        loadTradingDataFromFile()
    }

    private fun loadTradingDataFromFile() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Get more useful info about the companies
                val inputStreamNasdaq = context.resources.openRawResource(R.raw.nasdaq_screener)
                val nasdaqStockData = nasdaqScreenerParser.parse(inputStreamNasdaq)
                _state.emit(_state.value.copy(nasdaqCompanyData = nasdaqStockData))

                // Parse TradingData
                val inputStream = context.resources.openRawResource(R.raw.trading_data)
                val parsedData = tradingDataParser.parse(inputStream)
                _state.emit(_state.value.copy(tradingData = parsedData))

                // Update Last Week's data first
                // TODO: remove the referenceDate when deploying
                val lastWeekData = filterWeekData(referenceDate)
                val weekBeforeLastData = filterWeekBeforeLastData(referenceDate)
                _state.emit(
                    _state.value.copy(
                        lastWeekData = lastWeekData,
                        weekBeforeLastData = weekBeforeLastData
                        )
                )

                updateTradingAnalysisState()
            } catch (e: Exception) {
                Log.e("TradingAnalysis", "Error loading trading data", e)
            }
        }
    }

    fun loadTradingData(tradingData: List<TradingDataEntry>) {
        viewModelScope.launch {
            _state.emit(_state.value.copy(tradingData = tradingData))
            updateTradingAnalysisState()
        }
    }

    private fun updateTradingAnalysisState() {
        viewModelScope.launch {
            val clearing = calculateClearingInfo()
            _state.emit(
                _state.value.copy(
                    // V 1.0
                    dailyVolumeTrend = calculateDailyVolumeTrend(),
                    transactionAmountDistribution = calculateTransactionAmountDistribution(),
                    userActivePeriods = calculateUserActivePeriods(),
                    userCategoryPreferences = calculateUserCategoryPreferences(),
                    weeklyTransactionAnalysis = calculateWeeklyTransactionAnalysis(referenceDate), // TODO: Remove referenceData when Deploying

                    // V 2.0

                    // Update the rest
                    clearings = clearing,

                    weeklyTotalTrades = calculateWeeklyTotalTrades(),
                    weeklyTradeGrowthPercentage = calculateWeeklyTradeGrowth(),
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
                    mostActiveSellCount = calculateMostActiveSellCount(),
                    weeklyTransactionChange = calculateWeeklyChange(calculateWeeklyTransactionAnalysis(referenceDate)),
                    companyPreferences = calculateCompanyPreferences(),
                    companyWinRate = calculateStockWinRates(clearing),

                )
            )
        }
    }


    // Add methods for processing tradingData and returning chart data
    private fun calculateDailyVolumeTrend(): Map<String, Double> {
        val dailyVT = _state.value.tradingData.groupBy { it.createdAt.substring(0, 10) } // Group by date
            .mapValues { (_, entries) ->
                entries.sumOf { it.filledAssetQuantity }
            }
        return dailyVT.mapKeys {
            it.key.substring(5, 10)
        }
    }

    private fun calculateTransactionAmountDistribution(): Map<String, Double> {
        return _state.value.lastWeekData.groupBy { it.side } // Group by TradeSide (BUY/SELL)
            .mapKeys { (side, _) -> side.toString() } // 将 TradeSide 转换为 String
            .mapValues { (_, entries) ->
                entries.sumOf { entry ->
                    entry.averagePrice * entry.filledAssetQuantity
                }
            }
    }


    private fun calculateUserActivePeriods(): Map<String, Double> {
        return _state.value.lastWeekData
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
                entries.sumOf { it.filledAssetQuantity }
            }
            .toSortedMap(compareBy {
                when (it) {
                    "Pre-Market" -> 0
                    "Post-Market" -> Int.MAX_VALUE
                    else -> it.substringBefore(":").toInt() // Sort by Hours
                }
            })
    }


    private fun calculateUserCategoryPreferences(): Map<String, Double> {
        val nasdaqIndustryMap = _state.value.nasdaqCompanyData.associateBy { it.symbol }

        return _state.value.lastWeekData.groupBy { it.symbol }
            .mapNotNull { (symbol, entries) ->
                val industry = nasdaqIndustryMap[symbol]?.industry ?: return@mapNotNull null
                industry to entries.size.toDouble()
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, sizes) -> sizes.sum() }
    }

    @SuppressLint("DefaultLocale")
    private fun calculateWeeklyTransactionAnalysis(referenceDate: java.time.LocalDate = java.time.LocalDate.now()): Map<String, Pair<Double,Double>> {
        val recentWeeklyData = (0 until 4).associate { weeksAgo ->
            val weekData = filterWeekData(referenceDate, weeksAgo).filter{it.state != OrderState.CANCELLED}
            val weekStartDate = getWeekRange(referenceDate, weeksAgo).first.toString()

            val totalBuy = weekData.filter { it.side == TradeSide.BUY }
                .sumOf { it.filledAssetQuantity * it.averagePrice }

            val totalSell = weekData.filter { it.side == TradeSide.SELL }
                .sumOf { it.filledAssetQuantity * it.averagePrice }

            weekStartDate.takeLast(5) to (totalBuy.let { String.format("%.2f", it).toDouble() } to totalSell.let { String.format("%.2f", it).toDouble() })
        }
        Log.d("calculateWeeklyTransactionAnalysis", "calculateWeeklyTransactionAnalysis: $recentWeeklyData")
        return recentWeeklyData
    }

    @SuppressLint("DefaultLocale")
    private fun calculateWeeklyChange(weeklyTransactionAnalysis: Map<String, Pair<Double, Double>>): Double {
        val currentYear = java.time.LocalDate.now().year

        val sortedEntries = weeklyTransactionAnalysis.entries
            .sortedByDescending { java.time.LocalDate.parse("$currentYear-${it.key}") } // 动态生成完整日期
            .take(2)

        if (sortedEntries.size < 2) {
            return 0.0
        }

        val thisWeek = sortedEntries[0].value.let { it.first + it.second }
        val lastWeek = sortedEntries[1].value.let { it.first + it.second }

        return if (lastWeek > 0) {
            ((thisWeek - lastWeek) / lastWeek * 100).let { String.format("%.2f", it).toDouble() }
        } else {
            0.0
        }
    }

    // V 2.0

    private fun getWeekRange(referenceDate: java.time.LocalDate = java.time.LocalDate.now(), weeksAgo: Int = 1): Pair<java.time.LocalDate, java.time.LocalDate> {
        // Helper function to get the dates for weeks
        val startOfWeek = referenceDate.minusWeeks(weeksAgo.toLong()).with(java.time.DayOfWeek.MONDAY)
        val endOfWeek = referenceDate.minusWeeks(weeksAgo.toLong()).with(java.time.DayOfWeek.SUNDAY)
        Log.d("getWeekRange", "startOfWeek: $startOfWeek")
        Log.d("getWeekRange", "endOfWeek: $endOfWeek")
        return Pair(startOfWeek, endOfWeek)
    }

    private fun filterWeekData(referenceDate: java.time.LocalDate = java.time.LocalDate.now(), weeksAgo:Int = 1): List<TradingDataEntry> {
        // Helper function to get the data for that specific week
        val (startOfLastWeek, endOfLastWeek) = getWeekRange(referenceDate, weeksAgo = weeksAgo)
        val lastWeekData = _state.value.tradingData.filter {
            val date = java.time.LocalDate.parse(it.createdAt.substring(0, 10))
            date in startOfLastWeek..endOfLastWeek
        }
        return lastWeekData
    }

    private fun filterWeekBeforeLastData(referenceDate: java.time.LocalDate = java.time.LocalDate.now()): List<TradingDataEntry> {
        val (startOfWeekBeforeLast, endOfWeekBeforeLast) = getWeekRange(referenceDate, weeksAgo = 2)
        return _state.value.tradingData.filter {
            val date = java.time.LocalDate.parse(it.createdAt.substring(0, 10))
            date in startOfWeekBeforeLast..endOfWeekBeforeLast
        }
    }

    // 总交易次数
    private fun calculateWeeklyTotalTrades(): Int {
        Log.d("calculateWeeklyTotalTrades", "calculateWeeklyTotalTrades: ${_state.value.lastWeekData.size}")
        return _state.value.lastWeekData.size
    }

    // 交易增长百分比
    @SuppressLint("DefaultLocale")
    private fun calculateWeeklyTradeGrowth(): Double {
        val currentTrades = _state.value.lastWeekData.size
        val lastWeekTrades = _state.value.weekBeforeLastData.size
        return if (lastWeekTrades != 0){
            String.format("%.2f", ((currentTrades - lastWeekTrades).toDouble() / lastWeekTrades) * 100).toDouble()
        } else 0.0

    }

    // 做 T 的交易次数
    private fun calculateTotalTTrades(): Int {
        return _state.value.lastWeekData
            .groupBy { it.symbol to it.createdAt.substring(0, 10) } // Group by symbol and date
            .count { (_, trades) -> trades.size > 1 } // Count groups with more than one trade
    }

    // 做 T 的成功率
    @SuppressLint("DefaultLocale")
    private fun calculateSuccessfulTradePercentage(): Double {
        val tTrades = _state.value.lastWeekData.filter { it.type == OrderType.STOP_LIMIT || it.type == OrderType.STOP_LOSS }
        val successfulTrades = tTrades.count { it.state == OrderState.CLOSED }
        return if (tTrades.isNotEmpty()) {
            String.format("%.2f", (successfulTrades.toDouble() / tTrades.size) * 100).toDouble()
        } else 0.0
    }

    // 交易的股票总数
    private fun calculateTotalStocksTraded(): Int {
        return _state.value.lastWeekData.map { it.symbol }.distinct().size
    }

    // 上周新增持仓的股票数量
    private fun calculateStocksCurrentlyHeld(): Int {
        return _state.value.lastWeekData.filter { it.state != OrderState.CLOSED }.map { it.symbol }.distinct().size
    }

    // 已清仓的股票数量 : TODO: 逻辑有问题
    private fun calculateStocksCleared(): Int {
        val totalStocks = _state.value.tradingData.map { it.symbol }.distinct().size
        val heldStocks = _state.value.tradingData.filter { it.state != OrderState.CLOSED }.map { it.symbol }.distinct().size
        return totalStocks - heldStocks
    }

    // 交易次数最多的股票
    private fun calculateMostTradedStock(): String {
        return _state.value.lastWeekData.groupingBy { it.symbol }.eachCount().maxByOrNull { it.value }?.key.orEmpty()
    }

    // 最活跃的行业
    private fun calculateMostActiveSector(): String {
        val nasdaqIndustryMap = _state.value.nasdaqCompanyData.associateBy { it.symbol }
        return _state.value.lastWeekData.mapNotNull { entry ->
            nasdaqIndustryMap[entry.symbol]?.industry
        }.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key.orEmpty()
    }

    // 最活跃的买入时间段
    private fun calculateMostActiveBuyTime(): String {
        return _state.value.lastWeekData.filter { it.side == TradeSide.BUY }
            .groupBy { it.createdAt.substring(11, 13) } // 按小时分组
            .maxByOrNull { (_, entries) -> entries.size }
            ?.key?.let { "$it:00-$it:59" }.orEmpty()
    }

    // 最活跃的买入交易数量
    private fun calculateMostActiveBuyCount(): Int {
        val buyTimes = _state.value.lastWeekData.filter { it.side == TradeSide.BUY }
            .groupingBy { it.createdAt.substring(11, 13) } // 按小时分组
            .eachCount()
        val mostActiveTime = buyTimes.maxByOrNull { it.value }?.key
        return buyTimes[mostActiveTime] ?: 0
    }

    // 最活跃的卖出时间段
    private fun calculateMostActiveSellTime(): String {
        return _state.value.lastWeekData.filter { it.side == TradeSide.SELL }
            .groupBy { it.createdAt.substring(11, 13) } // 按小时分组
            .maxByOrNull { (_, entries) -> entries.size }
            ?.key?.let { "$it:00-$it:59" }.orEmpty()
    }

    // 最活跃的卖出交易数量
    private fun calculateMostActiveSellCount(): Int {
        val sellTimes = _state.value.lastWeekData.filter { it.side == TradeSide.SELL }
            .groupingBy { it.createdAt.substring(11, 13) } // 按小时分组
            .eachCount()
        val mostActiveTime = sellTimes.maxByOrNull { it.value }?.key
        return sellTimes[mostActiveTime] ?: 0
    }

    @SuppressLint("DefaultLocale")
    private fun calculateClearingInfo(): List<ClearingInfo> {
        // return type: Pair<List<ClearingInfo>, Map<String,Pair<Double, Double>>>
//        var clearings: List<ClearingInfo> = emptyList()
        val clearings = mutableListOf<ClearingInfo>()
        val holdings = mutableMapOf<String, Pair<Double, Double>>()

        val groupedAndSortedTradingData = _state.value.lastWeekData
            .filter { it.state != OrderState.CANCELLED }
            .groupBy { it.symbol }
            .mapValues { (_, entries) ->
                entries.sortedBy { it.createdAt }
            }

        for ((symbol, stockTradingData) in groupedAndSortedTradingData) {
            var currentHoldings = 0.0 // 当前持有的股票数量
            var holdingCost = 0.0 // 当前持有的总成本
            var lastClearingTrades = mutableListOf<TradingDataEntry>() // 记录上次清仓后的交易
            var earliestBuyPrice = 0.0 // 本周最早买入价格
            var isFirstBuyFound = false // 标记是否找到本周的第一笔买入价格
            var successCount = 0
            var failureCount = 0

            var netProfit = 0.0
            var totalBuyCost = 0.0
            var profitPercentage = 0.0
            var clearedCount = 0

            for (trade in stockTradingData) {
                Log.d("calculateClearingInfo", "trade: $trade")

                if (trade.side == TradeSide.BUY) {
                    // 更新当前持仓和成本
                    currentHoldings += trade.filledAssetQuantity
                    holdingCost += trade.filledAssetQuantity * trade.averagePrice
                    lastClearingTrades.add(trade)

                    // 更新本周最早买入价格
                    if (!isFirstBuyFound) {
                        earliestBuyPrice = trade.averagePrice
                        isFirstBuyFound = true
                    }
                } else if (trade.side == TradeSide.SELL) {
                    // 计算卖出产生的收益
                    var sellQuantity = trade.filledAssetQuantity
                    val sellRevenue = sellQuantity * trade.averagePrice
                    var tempBuyCost = 0.0

                    while (sellQuantity > 0) {
                        if (lastClearingTrades.isEmpty() || currentHoldings <= 0) {
                            // 如果列表为空，停止处理，防止异常
                            currentHoldings -= sellQuantity
                            sellQuantity -= sellQuantity
                            clearedCount += 1
                            break
                        }

                        val buyTrade = lastClearingTrades.removeAt(0)
                        val quantityToMatch = minOf(buyTrade.filledAssetQuantity, sellQuantity)

                        tempBuyCost += quantityToMatch * buyTrade.averagePrice
                        currentHoldings -= quantityToMatch
                        sellQuantity -= quantityToMatch
                        holdingCost -= quantityToMatch * buyTrade.averagePrice

                        if (quantityToMatch < buyTrade.filledAssetQuantity) {
                            lastClearingTrades.add(
                                0,
                                buyTrade.copy(filledAssetQuantity = buyTrade.filledAssetQuantity - quantityToMatch)
                            )
                        }
                        Log.d("currentHoldings", "currentHoldings, sellQuantity: $currentHoldings, $sellQuantity")
                    }
                    Log.d("currentHoldings", "currentHoldings, sellQuantity resolved: $currentHoldings, $sellQuantity")
                    if (currentHoldings < 0 && earliestBuyPrice != 0.0){
                        totalBuyCost -= currentHoldings *  earliestBuyPrice
                        currentHoldings = 0.0
                    } else {
                        // The calculation here is estimated not real. Future Fix Needed.
                        // Not adding more for now
                    }
                    // 计算清仓收益
                    val newProfitGain = sellRevenue - tempBuyCost

                    // Accumulate all costs
                    totalBuyCost += tempBuyCost

                    if (newProfitGain >0){successCount += 1} else failureCount += 1

                    netProfit += newProfitGain

                }

            }
            Log.d("calculateClearingInfo", "___entries end___")
            profitPercentage = netProfit / totalBuyCost



            var averageCost: Double = -1.0

            if (currentHoldings > 0.0){
                averageCost = holdingCost / currentHoldings
            }

            holdings[symbol] = Pair(averageCost, currentHoldings)

            clearings.add(
                ClearingInfo(
                    symbol = symbol,
                    netProfit = String.format("%.2f", netProfit).toDouble(),
                    profitPercentage = String.format("%.2f", profitPercentage).toDouble(),
                    successCount = successCount,
                    failureCount = failureCount,
                    holdings = holdings,
                    clearedCount = clearedCount
                )
            )
            Log.d("calculateClearingInfo", "clearings: $clearings")
        }

        return clearings
    }

    @SuppressLint("DefaultLocale")
    private fun calculateStockWinRates(clearings: List<ClearingInfo>): Map<String, Double> {
        return clearings.associate { info ->
            val totalTrades = info.successCount + info.failureCount
            val winRate = if (totalTrades > 0) info.successCount.toDouble() / totalTrades else 0.0
            info.symbol to String.format("%.2f", winRate * 100).toDouble()
        }
    }

    private fun calculateCompanyPreferences(): Map<String, Double> {
        return _state.value.lastWeekData.groupBy { it.symbol } // 按股票代码分组
            .mapValues { (_, entries) -> entries.size.toDouble() } // 统计每只股票的交易次数
    }


}