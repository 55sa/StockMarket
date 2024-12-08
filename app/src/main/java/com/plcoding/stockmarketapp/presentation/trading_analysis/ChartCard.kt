package com.plcoding.stockmarketapp.presentation.trading_analysis


import android.util.Log
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.*

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp


import ir.ehsannarmani.compose_charts.RowChart
import ir.ehsannarmani.compose_charts.*
import ir.ehsannarmani.compose_charts.models.*
import kotlin.math.log


@Composable
fun CheckboxWithLabel(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = isChecked, onCheckedChange = onCheckedChange)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.body1)
    }
}

@Composable
fun ChartCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 4.dp
    ) {
        content()
    }
}


@Composable
fun LineChartContent(data: List<Pair<String, Double>>) {
    data.map { it.second }.maxOrNull()?.let { maxValue ->
        LineChart(
            data = remember {
                listOf(
                    Line(
                        label = "Line Data",
                        values = data.map { it.second },
                        color = SolidColor(Color.Red)
                    )
                )
            },
            zeroLineProperties = ZeroLineProperties(
                enabled = true,
                color = SolidColor(Color.Gray),
            ),
            minValue = 0.0,
            maxValue = maxValue + 100
        )
    }
}

@Composable
fun ColumnChartContent(data: List<Pair<String, Double>>) {
    data.map { it.second }.maxOrNull()?.let { maxValue ->
        ColumnChart(
            data = remember {
                data.map {
                    Bars(
                        label = it.first,
                        values = listOf(Bars.Data(label = "Value", value = it.second, color = SolidColor(Color.Green)))
                    )
                }
            },
            barProperties = BarProperties(
                cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
                spacing = 8.dp,
                thickness = 20.dp
            )
        )
    }
}


@Composable
fun RowChartContent(data: Map<String, Double>) {
    data.entries.map { it.value }.maxOrNull()?.let { maxValue ->
        RowChart(
            data = remember {
                data.entries.map { entry ->
                    Bars(
                        label = entry.key,
                        values = listOf(
                            Bars.Data(value = entry.value, color = SolidColor(Color.Blue))
                        )
                    )
                }
            },
            minValue = 0.0,
            maxValue = maxValue + 10
        )
    }
}

@Composable
fun ChartScreen(viewModel: TradingAnalysisViewModel) {
    val state = viewModel.state.collectAsState().value
//    val scrollState  = rememberScrollState()

//    LazyColumn(modifier = Modifier.verticalScroll(scrollState)){
//        item {
//
//        }
//    }
    Log.d("ChartScreen", "ChartScreen: before cartselector ")
//    ChartSelector(
//        state = state,
//        onToggleChart = { chartKey ->
//            viewModel.toggleChart { currentState ->
//                when (chartKey) {
//                    "showDailyVolumeTrend" -> currentState.copy(showDailyVolumeTrend = !currentState.showDailyVolumeTrend)
//                    "showTransactionAmountDistribution" -> currentState.copy(showTransactionAmountDistribution = !currentState.showTransactionAmountDistribution)
//                    "showUserActivePeriods" -> currentState.copy(showUserActivePeriods = !currentState.showUserActivePeriods)
//                    "showUserCategoryPreferences" -> currentState.copy(showUserCategoryPreferences = !currentState.showUserCategoryPreferences)
//                    "showMonthlyTransactionAnalysis" -> currentState.copy(showMonthlyTransactionAnalysis = !currentState.showMonthlyTransactionAnalysis)
//                    "showProfitLossDistribution" -> currentState.copy(showProfitLossDistribution = !currentState.showProfitLossDistribution)
//                    else -> currentState
//                }
//            }
//        }
//    )
    ShowSelectedChart(state = state)
    Log.d("ChartScreen", "ChartScreen: after cartselector ")






}

@Composable
fun ShowSelectedChart(state: TradingAnalysisState){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .height(10.dp)
    ) {
        item {
                ChartCard(title = "Trading Volume Trend") {
                    LineChartContent(data = state.dailyVolumeTrend)
                }
            }
        // Charts based on selection
//        if (state.showDailyVolumeTrend) {
//            item {
//                ChartCard(title = "Trading Volume Trend") {
//                    LineChartContent(data = state.dailyVolumeTrend)
//                }
//            }
//        }
//        if (state.showTransactionAmountDistribution) {
//            item {
//                ChartCard(title = "Transaction Amount Distribution") {
//                    RowChartContent(data = state.transactionAmountDistribution.mapKeys { it.key.name })
//                }
//            }
//        }
//        if (state.showUserActivePeriods) {
//            item {
//                ChartCard(title = "User Active Periods") {
//                    ColumnChartContent(data = state.userActivePeriods)
//                }
//            }
//        }
//        if (state.showUserCategoryPreferences) {
//            item {
//                ChartCard(title = "User Category Preferences") {
//                    RowChartContent(data = state.userCategoryPreferences)
//                }
//            }
//        }
//        if (state.showMonthlyTransactionAnalysis) {
//            item {
//                ChartCard(title = "Monthly Transaction Analysis") {
//                    ColumnChartContent(data = state.monthlyTransactionAnalysis.toList())
//                }
//            }
//        }
//        if (state.showProfitLossDistribution) {
//            item {
//                ChartCard(title = "Profit Loss Distribution") {
//                    RowChartContent(data = state.profitLossDistribution.mapKeys { it.key.name })
//                }
//            }
//        }
    }
}

@Composable
fun ChartSelector(
    state: TradingAnalysisState,
    onToggleChart: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
        Button(onClick = { expanded = !expanded }) {
            Text("Select Charts")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(onClick = { onToggleChart("showDailyVolumeTrend") }) {
                CheckboxWithLabel(
                    label = "Trading Volume Trend",
                    isChecked = state.showDailyVolumeTrend,
                    onCheckedChange = { onToggleChart("showDailyVolumeTrend") }
                )
            }
            DropdownMenuItem(onClick = { onToggleChart("showTransactionAmountDistribution") }) {
                CheckboxWithLabel(
                    label = "Transaction Amount Distribution",
                    isChecked = state.showTransactionAmountDistribution,
                    onCheckedChange = { onToggleChart("showTransactionAmountDistribution") }
                )
            }
            DropdownMenuItem(onClick = { onToggleChart("showUserActivePeriods") }) {
                CheckboxWithLabel(
                    label = "User Active Periods",
                    isChecked = state.showUserActivePeriods,
                    onCheckedChange = { onToggleChart("showUserActivePeriods") }
                )
            }
            DropdownMenuItem(onClick = { onToggleChart("showUserCategoryPreferences") }) {
                CheckboxWithLabel(
                    label = "User Category Preferences",
                    isChecked = state.showUserCategoryPreferences,
                    onCheckedChange = { onToggleChart("showUserCategoryPreferences") }
                )
            }
            DropdownMenuItem(onClick = { onToggleChart("showMonthlyTransactionAnalysis") }) {
                CheckboxWithLabel(
                    label = "Monthly Transaction Analysis",
                    isChecked = state.showMonthlyTransactionAnalysis,
                    onCheckedChange = { onToggleChart("showMonthlyTransactionAnalysis") }
                )
            }
            DropdownMenuItem(onClick = { onToggleChart("showProfitLossDistribution") }) {
                CheckboxWithLabel(
                    label = "Profit Loss Distribution",
                    isChecked = state.showProfitLossDistribution,
                    onCheckedChange = { onToggleChart("showProfitLossDistribution") }
                )
            }
        }
    }
}
