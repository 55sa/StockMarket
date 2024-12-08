package com.plcoding.stockmarketapp.presentation.trading_analysis

// Jetpack Compose
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Compose Charts
import ir.ehsannarmani.compose_charts.*
import ir.ehsannarmani.compose_charts.models.*

// Android tool
import android.util.Log
import androidx.compose.runtime.collectAsState

import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.plcoding.stockmarketapp.domain.model.TradeSide
import com.plcoding.stockmarketapp.presentation.Main_Screen.BottomNavigationBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun TradingAnalysisScreen(navigator: DestinationsNavigator,
                          viewModel: TradingAnalysisViewModel = hiltViewModel()
) {

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navigator = navigator
            )
        }
    ) { innerPadding ->
        DrawCharts(viewModel)
    }
}

@Composable
fun DrawCharts(viewModel: TradingAnalysisViewModel){

    val state = viewModel.state.collectAsState().value

    Column(modifier = Modifier.padding(16.dp)) {
        // Checkboxes
        CheckboxWithLabel(
            label = "Trading Volume Trend",
            isChecked = state.showDailyVolumeTrend,
            onCheckedChange = { viewModel.toggleChart { it.copy(showDailyVolumeTrend = !it.showDailyVolumeTrend) } }
        )
        CheckboxWithLabel(
            label = "Trading Transaction",
            isChecked = state.showTransactionAmountDistribution,
            onCheckedChange = {
                viewModel.toggleChart {
                    it.copy(
                        showTransactionAmountDistribution = !it.showTransactionAmountDistribution
                    )
                }
            }
        )
        CheckboxWithLabel(
            label = "User Active Periods",
            isChecked = state.showUserActivePeriods,
            onCheckedChange = { viewModel.toggleChart { it.copy(showUserActivePeriods = !it.showUserActivePeriods) } }
        )
        CheckboxWithLabel(
            label = "User Category Preferences",
            isChecked = state.showUserCategoryPreferences,
            onCheckedChange = { viewModel.toggleChart { it.copy(showUserCategoryPreferences = !it.showUserCategoryPreferences) } }
        )
        CheckboxWithLabel(
            label = "Monthly Transaction Analysis",
            isChecked = state.showMonthlyTransactionAnalysis,
            onCheckedChange = { viewModel.toggleChart { it.copy(showMonthlyTransactionAnalysis = !it.showMonthlyTransactionAnalysis) } }
        )
        CheckboxWithLabel(
            label = "Profit Loss Distribution",
            isChecked = state.showProfitLossDistribution,
            onCheckedChange = { viewModel.toggleChart { it.copy(showProfitLossDistribution = !it.showProfitLossDistribution) } }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Charts
        if (state.showDailyVolumeTrend) {
            Log.d("testing", "TradingAnalysisScreen: ${state.dailyVolumeTrend}")
            MyLineChart(title = "Trading Volume Trend", data = state.dailyVolumeTrend)
        }
        if (state.showTransactionAmountDistribution) {
            Text("Trading Transaction")
            MyRowChart(title = "交易金额分布", data = state.transactionAmountDistribution)

        }
        if (state.showUserActivePeriods) {
            Text("User Active Periods")
            MyColumnChart(title = "用户活跃时间段", data = state.userActivePeriods)

        }
        if (state.showUserCategoryPreferences) {
            Text("User Category Preferences")
            userCategoryPreferences(
                title = "用户交易品类偏好",
                data = state.userCategoryPreferences
            )
        }
        if (state.showMonthlyTransactionAnalysis) {
            Text("Monthly Transaction Analysis")
            MyColumnChart(
                title = "用户月度交易行为分析",
                data = state.monthlyTransactionAnalysis.toList()
            )

        }
        if (state.showProfitLossDistribution) {
            Text("Profit Loss Distribution")
            MyRowChart(title = "交易盈亏分布", data = state.profitLossDistribution)

        }
    }
}

@Composable
fun CheckboxWithLabel(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { isChecked -> onCheckedChange(isChecked) }
        )
        Text(
            text = label,
            style = MaterialTheme.typography.h5
        )
    }
}

@Composable
fun MyLineChart(title: String, data: List<Pair<String, Double>>) {
    // draw a line chart in a card
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, style = MaterialTheme.typography.h5)
        // Placeholder for actual chart
        data.map { it.second }.maxOrNull()?.let {
            LineChart(
                data = remember {
                    listOf(
                        Line(
                            label = "not Temperature",
    //                        values = listOf(28.0, 41.0, -5.0, 10.0, 35.0),
                            values = data.map { it.second },
                            color = SolidColor(Color.Red)
                    ),
                    )
                },
                zeroLineProperties = ZeroLineProperties(
                    enabled = true,
                    color = SolidColor(Color.Red),
                ),
                minValue = 0.0,
                maxValue = it + 100
            )
        }
    }
}


@Composable
fun MyColumnChart(title: String, data: List<Pair<String, Double>>) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, style = MaterialTheme.typography.h5)
        // Placeholder for actual chart
        data.map { it.second }.maxOrNull()?.let {
            ColumnChart(
                data = remember {
                    data.map{
                        Bars(
                            label = it.first,
                            values = listOf(Bars.Data(label = "Linux", value = it.second, color = SolidColor(Color.Red)))
                        )
                    }
                },
                barProperties = BarProperties(
                    cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
                    spacing = 3.dp,
                    thickness = 20.dp
                ),
            )
        }
    }
}


@Composable
fun MyRowChart(title: String, data: Map<TradeSide, Double>) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, style = MaterialTheme.typography.h5)
        data.entries.map { it.value }.minOrNull()?.let {
            RowChart(
                data = remember {
                    data.entries.mapIndexed { index, entry ->
                        val color =
                            if (entry.key == TradeSide.BUY) SolidColor(Color.Blue) else SolidColor(
                                Color.Red
                            )
                        Bars(
                            label = (index + 1).toString(), // Labels as 1, 2, ...
                            values = listOf(
                                Bars.Data(value = entry.value, color = color)
                            )
                        )
                    }
                },
//                maxValue = 75.0,
                minValue = it
            )
        }
    }
}



@Composable
fun userCategoryPreferences(title: String, data: Map<String, Double>) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, style = MaterialTheme.typography.h5)
        data.entries.map { it.value }.minOrNull()?.let {
            RowChart(
                data = remember {
                    data.entries.mapIndexed { index, entry ->
//                        val color = SolidColor(Color.Blue)
//                        val color = SolidColor(Color(entry.key.hashCode()))
                        Bars(
                            label = entry.key, // Labels as 1, 2, ...
                            values = listOf(
                                Bars.Data(value = entry.value, color = SolidColor(Color.Blue))
                            )
                        )
                    }
                },
//                maxValue = 75.0,
                minValue = 0.0
            )
        }
    }
}