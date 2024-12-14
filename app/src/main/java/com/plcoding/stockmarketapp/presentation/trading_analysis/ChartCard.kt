package com.plcoding.stockmarketapp.presentation.trading_analysis


import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp


import ir.ehsannarmani.compose_charts.RowChart
import ir.ehsannarmani.compose_charts.*
import ir.ehsannarmani.compose_charts.models.*

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.times

sealed class ChartType {
    data class Line(val data: List<Map<String, Double>>, val labels: List<String>) : ChartType()
    data class Column(val data: List<Map<String, Double>>, val labels: List<String>) : ChartType()
    data class Row(val data: List<Map<String, Double>>, val labels: List<String>) : ChartType()

    @Composable
    fun Display(isToggled: Boolean = false) {
        when (this) {
            is Line -> LineChartContent(data, labels, isToggled)
            is Column -> ColumnChartContent(data, labels, isToggled)
            is Row -> RowChartContent(data, labels, isToggled)
        }
    }
}

data class AnnotatedText(
    val text: String,
    val highlighted: String,
    val suffix: String? = null,
    val highlighted2: String? = null,
    val suffix2: String? = null,
    val highlighted3: String? = null
)


@Composable
fun ChartScreen(viewModel: TradingAnalysisViewModel) {

    val state = viewModel.state.collectAsState().value

    // V 2.0

    // toggle Indices
    val toggleIndices1 = emptyList<Int>()

    // Group 1
    val chartGroup1 = listOf(
        "Monthly Trading Trend" to ChartType.Line(
            listOf(state.dailyVolumeTrend),
            listOf("Last Month")
        ),
        "Sector Preferences" to ChartType.Column(
            listOf(state.userCategoryPreferences),
            listOf("This Week")
        ),
        "Company Preferences" to ChartType.Column(
            listOf(state.companyPreferences),
            listOf("This Week")
        ),
        "Active Periods" to ChartType.Row(
            listOf(state.userActivePeriods),
            listOf("This Week")
        ),
    )
    // Analysis Group 1
    val analysisGroup1 = listOf(
        listOf( // chart 1
            "Total Trades" to AnnotatedText(
                text = "Last week, a total of ",
                highlighted = "${state.weeklyTotalTrades}",
                suffix = " trades were made. Compared to the previous week, there was a ",
                highlighted2 = "${state.weeklyTradeGrowthPercentage}%",
                suffix2 = " change."
            ),
            "T Trades" to AnnotatedText(
                text = "A total of ",
                highlighted = "${state.totalTTrades}",
                suffix = " T trades were made with a success rate of ",
                highlighted2 = "${state.successfulTradePercentage}%"
            ),
        ),
        listOf( // chart 2
            "Stocks Summary" to AnnotatedText(
                text = "Last week, ",
                highlighted = "${state.totalStocksTraded}",
                suffix = " stocks were traded, currently holding ",
                highlighted2 = "${ state.clearings.filter { clearingInfo ->
                    clearingInfo.holdings.values.none { it.second > 0.0 }
                }.size}",
                suffix2 = " stocks, and cleared ",
                highlighted3 = "${state.clearings.sumOf { it.clearedCount }} times"
            ),
            "Most Traded Stock" to AnnotatedText(
                text = "The most traded stock was: ",
                highlighted = state.mostTradedStock
            ),
            "Active Sector" to AnnotatedText(
                text = "The most active sector was: ",
                highlighted = state.mostActiveSector
            ),
        ),
        listOf( // chart 3
            "Stocks Summary" to AnnotatedText(
                text = "Last week, ",
                highlighted = "${state.totalStocksTraded}",
                suffix = " stocks were traded, currently holding ",
                highlighted2 = "${ state.clearings.filter { clearingInfo ->
                    clearingInfo.holdings.values.none { it.first == -1.0 }
                }.size}",
                suffix2 = " stocks, and cleared ",
                highlighted3 = "${state.clearings.sumOf { it.clearedCount }} times"
            ),
            "Most Traded Stock" to AnnotatedText(
                text = "The most traded stock was: ",
                highlighted = state.mostTradedStock
            ),
            "Active Sector" to AnnotatedText(
                text = "The most active sector was: ",
                highlighted = state.mostActiveSector
            ),
        ),
        listOf( // chart 4
            "Buying Time" to AnnotatedText(
                text = "The most active buying time was: ",
                highlighted = state.mostActiveBuyTime,
                suffix = ", with ",
                highlighted2 = "${state.mostActiveBuyCount} trades"
            ),
            "Selling Time" to AnnotatedText(
                text = "The most active selling time was: ",
                highlighted = state.mostActiveSellTime,
                suffix = ", with ",
                highlighted2 = "${state.mostActiveSellCount} trades"
            )
        )
    )

    // toggle indecy:

    val toggleIndices2 = listOf(0,1,3)

    // chartGroup2
    val chartGroup2 = listOf(
        "Net Profit" to ChartType.Column(
            data = listOf(
                state.clearings.associate { it.symbol to it.netProfit },
                state.lastWeekClearings.associate { it.symbol to it.netProfit }
            ) ,
            labels = listOf("This Week","Last Week")
        ),
        "Profit Ratio" to ChartType.Column(
            data = listOf(
                state.clearings.associate { it.symbol to it.profitPercentage },
                state.lastWeekClearings.associate { it.symbol to it.profitPercentage },
            ) ,
            labels = listOf("This Week %", "Last Week %")
        ),
        "Weekly Transaction Trend" to ChartType.Line(
            data = listOf(
                state.weeklyTransactionAnalysis.mapValues { (_, value) ->
                    value.first + value.second
                }

            ) ,
            labels = listOf("Dollars")
        ),
        "Trading Win Rate" to ChartType.Column(
            data = listOf(
                state.companyWinRate,
                state.lastWeekCompanyWinRate
            ),
            labels = listOf("This Week (%)","Last Week (%)")
        )
    )

    val analysisGroup2 = listOf(
        listOf(
            "Highest Profit" to AnnotatedText(
                text = "The stock with the highest profit last week: ",
                highlighted = "${state.clearings.maxByOrNull { it.netProfit }?.symbol}",
                suffix = " with net profit of ",
                highlighted2 = "\$${state.clearings.maxByOrNull { it.netProfit }?.netProfit}"
            ),
            "Lowest Profit" to AnnotatedText(
                text = "The stock with the lowest profit last week: ",
                highlighted = "${state.clearings.minByOrNull { it.netProfit }?.symbol}",
                suffix = " with net profit of ",
                highlighted2 = "\$${state.clearings.minByOrNull { it.netProfit }?.netProfit}"
            )
        ),
        listOf(
            "Highest Profit Ratio" to AnnotatedText(
                text = "The stock with the highest profit ratio last week: ",
                highlighted = "${state.clearings.maxByOrNull { it.profitPercentage }?.symbol}",
                suffix = " with profit ratio of ",
                highlighted2 = "${state.clearings.maxByOrNull { it.profitPercentage }?.profitPercentage}%"
            ),
            "Lowest Profit Ratio" to AnnotatedText(
                text = "The stock with the lowest profit ratio last week: ",
                highlighted = "${state.clearings.minByOrNull { it.profitPercentage }?.symbol}",
                suffix = " with profit ratio of ",
                highlighted2 = "${state.clearings.minByOrNull { it.profitPercentage }?.profitPercentage}%"
            )
        ),
        listOf(
            "Transaction Trend" to AnnotatedText(
                text = "Last week, total transaction is: $",
                highlighted = "${state.weeklyTransactionAnalysis.entries.maxByOrNull { it.key }?.value?.let { it.first + it.second } ?: 0.0}",
                suffix = " It changed by ",
                highlighted2 = "${state.weeklyTransactionChange}%"
            )
        ),
        listOf(
            "Win Rate Analysis" to AnnotatedText(
                text = "Highest win rate stock: ",
                highlighted = "${state.companyWinRate.maxByOrNull { it.value }?.key ?: "N/A"} (${state.companyWinRate.maxByOrNull { it.value }?.value ?: 0.0}%)",
                suffix = ", Lowest win rate stock: ",
                highlighted2 = "${state.companyWinRate.minByOrNull { it.value }?.key ?: "N/A"} (${state.companyWinRate.minByOrNull { it.value }?.value ?: 0.0}%)"
            )
        )

    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {


        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Trading Analysis
            item {
                // Analysis Group1
                AnalysisAndChartGroup(
                    title = "Trading Analysis",
                    analysisContent = analysisGroup1,
                    cards = chartGroup1,
                    toggleIndices = toggleIndices1
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Profit Analysis
            item {

                // Analysis Group2
                AnalysisAndChartGroup(
                    title = "Profit Analysis",
                    analysisContent = analysisGroup2,
                    cards = chartGroup2,
                    toggleIndices = toggleIndices2
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // add additional space to avoid overlay
            item {
                Spacer(modifier = Modifier.height(56.dp))
            }
        }

    }
}


@Composable
fun AnalysisAndChartGroup(title: String,
                          analysisContent: List<List<Pair<String, AnnotatedText>>>,
                          cards: List<Pair<String, ChartType>>,
                          toggleIndices: List<Int>
) {
    var currentIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .background(Color(0xFF1C1C2A), shape = RoundedCornerShape(16.dp))
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center // set to center
        ) {
            Text(
                text = title,
                style = TextStyle(
                    color = Color(0xFF1E88E5),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        // 卡片组件
        SwipableCardComponent(
            cards = cards,
            onCardIndexChange =  { newIndex ->
                currentIndex = newIndex },
            toggleIndices
        )

        Spacer(modifier = Modifier.height(8.dp))

        analysisContent.getOrNull(currentIndex)?.forEach { (header, content) ->
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = header,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 4.dp).padding(horizontal = 6.dp)
                )
                Column(
                    modifier = Modifier.padding(horizontal = 6.dp).fillMaxWidth()
                ) {
                    val annotatedText = buildAnnotatedString {
                        append(content.text)
                        withStyle(style = SpanStyle(color = Color(0xFF1E88E5))) {
                            append(content.highlighted)
                        }
                        content.suffix?.let { append(it) }
                        content.highlighted2?.let {
                            withStyle(style = SpanStyle(color = if (it.startsWith("-")) Color.Red else Color.Green)) {
                                append(it)
                            }
                        }
                        content.suffix2?.let { append(it) }
                        content.highlighted3?.let {
                            withStyle(style = SpanStyle(color = Color(0xFF1E88E5))) {
                                append(it)
                            }
                        }
                    }
                    Text(
                        text = annotatedText,
                        style = TextStyle(
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}


@Composable
fun SwipableCardComponent(cards: List<Pair<String, ChartType>>,
                          onCardIndexChange: (Int) -> Unit,
                          toggleIndices: List<Int>
) {
    var currentIndex by remember { mutableStateOf(0) }
    var dragOffset by remember { mutableStateOf(0f) }
    val animatedOffset by animateFloatAsState(targetValue = dragOffset)
    val switchStates = remember { mutableStateMapOf<Int, Boolean>() }



    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp) // the Height of the card
                .shadow(15.dp, RoundedCornerShape(25.dp))
                .offset(x = animatedOffset.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragOffset += dragAmount.x/3 // Reduce responsiveness
                        },
                        onDragEnd = {
                            if (dragOffset > 200) {
                                dragOffset = -300f // Move card off-screen to the right
                                currentIndex = (currentIndex - 1 + cards.size) % cards.size
                                onCardIndexChange(currentIndex)

                            } else if (dragOffset < -200) {
                                dragOffset = 300f  // Move card off-screen to the left
                                currentIndex = (currentIndex + 1) % cards.size
                                onCardIndexChange(currentIndex)
                            }
                            dragOffset = 0f
                        },
                        onDragCancel = {
                            dragOffset = 0f
                        }
                    )
                },
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2A2A3A),
                contentColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                ) {
                    // Title (Centered in the Box)
                    BasicText(
                        text = cards[currentIndex].first,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(4f, 4f),
                                blurRadius = 8f
                            )
                        ),
                        modifier = Modifier
                            .align(Alignment.Center) // Align text to the center of the Box
                            .padding(8.dp)
                    )

                    // Toggle content (Positioned in the bottom-right corner)
                    if (toggleIndices.contains(currentIndex)) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier
                                .align(Alignment.BottomEnd) // Align to bottom-right corner of the Box
                                .padding(8.dp)
                        ) {
                            BasicText(
                                text = "Compare",
                                style = TextStyle(
                                    color = Color.White.copy(alpha = 0.7f), // Subtle color for label
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            androidx.compose.material3.Switch(
                                checked = switchStates[currentIndex] ?: false,
                                onCheckedChange = { isChecked ->
                                    switchStates[currentIndex] = isChecked
                                },
                                modifier = Modifier.size(32.dp), // Adjust the size of the Switch
                                colors = androidx.compose.material3.SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF3AB3E8),
                                    uncheckedThumbColor = Color.Gray
                                )
                            )
                        }
                    }
                }



                Box(modifier = Modifier.padding(bottom = 16.dp)){
                    key(currentIndex){
                        if ((switchStates[currentIndex] == true) && toggleIndices.contains(currentIndex)) {
                            // Render the comparison chart when the switch is toggled on
                            // Replace this with a method to show the comparison chart
                            cards[currentIndex].second.Display(isToggled = true)
                        } else {
                            cards[currentIndex].second.Display()
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun LineChartContent(data: List<Map<String, Double>>, labels: List<String>, isToggled: Boolean = false) {

    if (data.isEmpty() || labels.isEmpty()) {
        Text("No data available", color = Color.Gray)
        return
    }

    // Convert each map in data to List<Pair<String, Double>> while sorting keys
    val chartData = if (isToggled) {
        // Convert all datasets
        data.map { map ->
            map.entries
                .sortedBy { it.key }
                .map { it.key to it.value }
        }
    } else {
        // Use only the first dataset
        data.firstOrNull()?.entries
            ?.sortedBy { it.key }
            ?.map { it.key to it.value }
            ?.let { listOf(it) } ?: emptyList()
    }

    // Find the maximum value across all datasets
    val maxValue = chartData
        .flatMap { it.map { pair -> pair.second } } // Extract all second values from all inner lists
        .maxOrNull() ?: 0.0 // Default to 0.0 if no data

    if (chartData.isEmpty() || maxValue == 0.0) {
        Text("No data to display", color = Color.Gray)
        return
    }

    // Prepare sampled labels for the x-axis
    val maxLabels = 5
    val sampledLabels = remember(chartData.first()) { // Use the first dataset's x-axis as the reference
        val keys = chartData.first().map { it.first }
        if (keys.size <= maxLabels) {
            keys
        } else {
            val step = (keys.size - 1) / (maxLabels - 1).toFloat()
            (0 until maxLabels).map { index -> keys[(index * step).toInt()] }
        }
    }

    LineChart(
        data = remember {
            // Create a Line for each dataset in `data`
            chartData.mapIndexed { index, pairs ->
                Line(
                    label = labels.getOrNull(index) ?: "Line ${index + 1}",
                    values = pairs.map { it.second }, // Extract values for this dataset
                    color = when (index) {
                        0 -> Brush.horizontalGradient(
                            colors = listOf(Color(0xFFDE942A), Color(0xFFB94621))
                        )
                        1 -> Brush.horizontalGradient(
                            colors = listOf(Color(0xFF3AB3E8), Color(0xFF0056A6))
                        )
                        2 -> Brush.horizontalGradient(
                            colors = listOf(Color(0xFF76C7C0), Color(0xFF2A9D8F))
                        )
                        else -> Brush.horizontalGradient(
                            colors = listOf(Color.Gray, Color.DarkGray)
                        )
                    },
                    firstGradientFillColor = when (index) {
                        0 -> Color(0xFFDE942A).copy(alpha = .5f)
                        1 -> Color(0xFF3AB3E8).copy(alpha = .5f)
                        2 -> Color(0xFF76C7C0).copy(alpha = .5f)
                        else -> Color.Gray.copy(alpha = .5f)
                    },
                    secondGradientFillColor = Color.Transparent,
                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                    gradientAnimationDelay = ((index + 1) * 500).toLong(), // Add delay between animations
                    drawStyle = DrawStyle.Stroke(width = 2.dp)
                )
            }
        },
        zeroLineProperties = ZeroLineProperties(
            enabled = true,
            color = SolidColor(Color.Gray),
        ),
        minValue = 0.0,
        maxValue = maxValue + 100,
        animationMode = AnimationMode.Together(delayBuilder = { it * 500L }),
        labelProperties = LabelProperties(
            enabled = true,
            labels = sampledLabels,
            textStyle = TextStyle.Default.copy(
                color = Color.White,
                fontSize = 12.sp
            ),
            padding = 5.dp, // Between Chart and label
            rotation = LabelProperties.Rotation(
                mode = LabelProperties.Rotation.Mode.Force,
                degree = -35f
            )
        ),
        indicatorProperties = HorizontalIndicatorProperties(
            textStyle = TextStyle.Default.copy(
                color = Color.White,
                fontSize = 12.sp
            )
        )
    )
}


@Composable
fun ColumnChartContent(data: List<Map<String, Double>>, labels: List<String>,isToggled: Boolean = false) {
    if (data.isEmpty() || labels.isEmpty()) {
        Text("No data available", color = Color.Gray)
        return
    }

    // Combine all unique keys from datasets to create a unified x-axis
    val chartData = if (isToggled) {
        // Combine all unique keys from all datasets
        data.flatMap { it.keys }
            .distinct()
            .sorted()
            .map { key ->
                key to data.map { it[key] ?: 0.0 } // Map each key to a list of values across datasets
            }
    } else {
        // Use only the first dataset
        data.firstOrNull()?.entries?.sortedBy { it.key }?.map { entry ->
            entry.key to listOf(entry.value)
        } ?: emptyList()
    }

    val adjustedThickness = if (isToggled) {
        maxOf(4.dp, 12.dp - (data.size - 1) * 4.dp) // Reduce thickness by 4 for each additional dataset, minimum 4.dp
    } else {
        12.dp // Default thickness when not toggled
    }

    val maxValue = chartData.flatMap { it.second }.maxOrNull() ?: 0.0
    if (chartData.isEmpty() || maxValue == 0.0) {
        Text("No data to display", color = Color.Gray)
        return
    }

    ColumnChart(
        modifier = Modifier.fillMaxSize().padding(horizontal = 22.dp),
        data = remember {
            chartData.map { (key, values) ->
                Bars(
                    label = if (key.length > 10) key.take(6) + "..." else key, // Shorten labels if too long
                    values = values.mapIndexed { index, value ->
                        Bars.Data(
                            label = labels.getOrNull(index) ?: "Dataset ${index + 1}",
                            value = value,
                            color = when (index) {
                                1 -> Brush.verticalGradient(
                                    colors = listOf(Color(0xFF3AB3E8), Color(0xFF0056A6))
                                )
                                0 -> Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFFA726), Color(0xFFFF5722))
                                )
                                2 -> Brush.verticalGradient(
                                    colors = listOf(Color(0xFF76C7C0), Color(0xFF2A9D8F))
                                )
                                else -> Brush.verticalGradient(
                                    colors = listOf(Color.Gray, Color.DarkGray)
                                )
                            }
                        )
                    }
                )
            }
        },
        barProperties = BarProperties(
            cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
            spacing = 3.dp,
            thickness = adjustedThickness
        ),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        labelProperties = LabelProperties(
            enabled = true,
            textStyle = TextStyle.Default.copy(
                color = Color.White,
                fontSize = 10.sp
            ),
            padding = 5.dp, // Between Chart and label
            rotation = LabelProperties.Rotation(
                mode = LabelProperties.Rotation.Mode.Force,
                degree = -35f
            )
        ),
        indicatorProperties = HorizontalIndicatorProperties(
            textStyle = TextStyle.Default.copy(
                color = Color.White,
                fontSize = 12.sp
            )
        )
    )
}

@Composable
fun RowChartContent(data: List<Map<String, Double>>, labels: List<String>,isToggled: Boolean = false) {
    if (data.isEmpty() || labels.isEmpty()) {
        Text("No data available", color = Color.Gray)
        return
    }

    // Combine all unique keys from datasets to create a unified x-axis
    val chartData = data.flatMap { it.keys }
        .distinct()
        .map { key ->
            key to data.map { it[key] ?: 0.0 } // Map each key to a list of values across datasets
        }

    val maxValue = chartData.flatMap { it.second }.maxOrNull() ?: 0.0
    if (chartData.isEmpty() || maxValue == 0.0) {
        Text("No data to display", color = Color.Gray)
        return
    }

    RowChart(
        data = remember {
            chartData.map { (key, values) ->
                Bars(
                    label = if (key.length > 10) key.take(8) + "..." else key, // Shorten labels if too long
                    values = values.mapIndexed { index, value ->
                        Bars.Data(
                            label = labels.getOrNull(index) ?: "Dataset ${index + 1}",
                            value = value,
                            color = when (index) {
                                0 -> Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFFFA726), Color(0xFFFF5722))
                                )
                                1 -> Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF3AB3E8), Color(0xFF0056A6))
                                )
                                2 -> Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF76C7C0), Color(0xFF2A9D8F))
                                )
                                else -> Brush.horizontalGradient(
                                    colors = listOf(Color.Gray, Color.DarkGray)
                                )
                            }
                        )
                    }
                )
            }
        },
        minValue = 0.0,
        maxValue = maxValue + 10,
        barProperties = BarProperties(
            cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
            spacing = 3.dp,
            thickness = 15.dp
        ),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        labelProperties = LabelProperties(
            enabled = true,
            textStyle = TextStyle.Default.copy(
                color = Color.White,
                fontSize = 12.sp
            ),
            padding = 5.dp, // Between Chart and label
            rotation = LabelProperties.Rotation(
                mode = LabelProperties.Rotation.Mode.Force,
                degree = -35f
            )
        ),
        indicatorProperties = VerticalIndicatorProperties(
            textStyle = TextStyle.Default.copy(
                color = Color.White,
                fontSize = 12.sp
            )
        )
    )
}