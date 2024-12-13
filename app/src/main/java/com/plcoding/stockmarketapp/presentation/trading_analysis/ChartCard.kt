package com.plcoding.stockmarketapp.presentation.trading_analysis


import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.Text

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.Spanned
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

sealed class ChartType {
    data class Line(val data: Map<String, Double>) : ChartType()
    data class Column(val data: Map<String, Double>) : ChartType()
    data class Row(val data: Map<String, Double>) : ChartType()

    @Composable
    fun Display() {
        when (this) {
            is Line -> LineChartContent(data)
            is Column -> ColumnChartContent(data)
            is Row -> RowChartContent(data)
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

    // V 1.0
    val cardsToDraw = listOf(
            "Daily Volume Trend" to ChartType.Line(
                state.dailyVolumeTrend
            ),
            "Transaction Amount Distribution" to ChartType.Column(
                state.transactionAmountDistribution
            ),
            "User Active Periods" to ChartType.Row(
                state.userActivePeriods
            ),

            "User Category Preferences" to ChartType.Column(
                state.userCategoryPreferences
            ),

            "Monthly Transaction Analysis" to ChartType.Column(
                state.monthlyTransactionAnalysis
            ),

            "Profit Loss Distribution" to ChartType.Column(
                state.profitLossDistribution
            )
        )

    // V 2.0
    val analysisGroup1 = listOf(
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
        "Stocks Summary" to AnnotatedText(
            text = "Last week, ",
            highlighted = "${state.totalStocksTraded}",
            suffix = " stocks were traded, currently holding ",
            highlighted2 = "${state.stocksCurrentlyHeld}",
            suffix2 = " stocks, and cleared ",
            highlighted3 = "${state.stocksCleared}"
        ),
        "Most Traded Stock" to AnnotatedText(
            text = "The most traded stock was: ",
            highlighted = state.mostTradedStock
        ),
        "Active Sector" to AnnotatedText(
            text = "The most active sector was: ",
            highlighted = state.mostActiveSector
        ),
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

    val cardsGroup1 = listOf(
        "Daily Volume Trend" to ChartType.Line(
            state.dailyVolumeTrend
        ),
        "User Active Periods" to ChartType.Row(
            state.userActivePeriods
        ),
        "Transaction Amount Distribution" to ChartType.Column(
            state.transactionAmountDistribution
        )
    )

    val analysisGroup2 = listOf(
        "Selling Time" to AnnotatedText(
            text = "The most active selling time was: ",
            highlighted = state.mostActiveSellTime,
            suffix = ", with ",
            highlighted2 = "${state.mostActiveSellCount} trades"
        )
    )

    val cardsGroup2 = listOf(


        "User Category Preferences" to ChartType.Column(
            state.userCategoryPreferences
        ),

        "Monthly Transaction Analysis" to ChartType.Column(
            state.monthlyTransactionAnalysis
        ),

        "Profit Loss Distribution" to ChartType.Column(
            state.profitLossDistribution
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        // 顶部显示APP名称
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E88E5)) // 蓝色背景
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "StockEasy",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                // 上半部分卡片组及分析
                AnalysisAndCardGroup(
                    title = "Volume & Transaction Analysis",
                    analysisContent = analysisGroup1,
                    cards = cardsGroup1
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {

                // 下半部分卡片组及分析
                AnalysisAndCardGroup(
                    title = "User Behavior & Profit Analysis",
                    analysisContent = analysisGroup2,
                    cards = cardsGroup2
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // add additional space to avoid overlay
            item {
                Spacer(modifier = Modifier.height(56.dp)) // 56dp 是典型导航栏高度，可以调整为您的实际高度
            }
        }

    }
}


@Composable
fun AnalysisAndCardGroup(title: String, analysisContent: List<Pair<String, AnnotatedText>>, cards: List<Pair<String, ChartType>>) {
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
        SwipableCardComponent(cards = cards)

        Spacer(modifier = Modifier.height(8.dp))

        analysisContent.forEach { (header, content) ->
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
                            withStyle(style = SpanStyle(color = if (it.contains("%") && it.startsWith("-")) Color.Red else Color.Green)) {
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
fun SwipableCardComponent(cards: List<Pair<String, ChartType>>) {
    var currentIndex by remember { mutableStateOf(0) }
    var dragOffset by remember { mutableStateOf(0f) }
    val animatedOffset by animateFloatAsState(targetValue = dragOffset)

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
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

                            } else if (dragOffset < -200) {
                                dragOffset = 300f  // Move card off-screen to the left
                                currentIndex = (currentIndex + 1) % cards.size
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
                    modifier = Modifier.padding(8.dp)
                )

                // Display the chart for the current card
                cards[currentIndex].second.Display()
            }
        }
    }
}


@Composable
fun LineChartContent(data: Map<String, Double>) {
    val chartData = data.entries
        .sortedBy { it.key } // 按键（例如日期）排序
        .map { it.key to it.value } // 转换为 List<Pair<String, Double>> 格式

    chartData.map { it.second }.maxOrNull()?.let { maxValue ->
        LineChart(
            data = remember {
                listOf(
                    Line(
                        label = "Line Data",
                        values = chartData.map { it.second }, // 提取数值部分
                        color = Brush.horizontalGradient(
                            colors = listOf(

                                Color(0xFFDE942A), // 橙色
                                Color(0xFFB94621)  // 深橙色
                            ),
                            startX = 0f,
                            endX = 1000f
                        ),
                        firstGradientFillColor = Color(0xFFDE942A).copy(alpha = .5f),
                        secondGradientFillColor = Color.Transparent,
                        strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                        gradientAnimationDelay = 1000,
                        drawStyle = DrawStyle.Stroke(width = 2.dp),
                    )
                )
            },
            zeroLineProperties = ZeroLineProperties(
                enabled = true,
                color = SolidColor(Color.Gray),
            ),
            minValue = 0.0,
            maxValue = maxValue + 100,
            animationMode = AnimationMode.Together(delayBuilder = {
                it * 500L
            })

        )
    }
}


@Composable
fun ColumnChartContent(data: Map<String, Double>) {
    val chartData = data.entries
        .sortedBy { it.key } // 按键（例如日期）排序
        .map { it.key to it.value } // 转换为 List<Pair<String, Double>> 格式

    chartData.map { it.second }.maxOrNull()?.let { maxValue ->
        ColumnChart(
            data = remember {
                chartData.map {
                    Bars(
                        label = it.first,
                        values = listOf(Bars.Data(label = "Value", value = it.second, color = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF3AB3E8), // 浅蓝色
                                Color(0xFFFFA726), // 橙色
                                Color(0xFFFF5722)  // 深橙色
                                ),
                                startX = 0f,
                                endX = 1000f
                                )
                            )
                        )
                    )
                }
            },
            barProperties = BarProperties(
                cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
                spacing = 3.dp,
                thickness = 12.dp
            ),
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),

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
                            Bars.Data(value = entry.value, color = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF3AB3E8), // 浅蓝色
                                    Color(0xFFFFA726), // 橙色
                                    Color(0xFFFF5722)  // 深橙色
                                ),
                                startX = 0f,
                                endX = 1000f // adjust according to screen brightness
                            )
                            )
                        )
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
        )
    }
}

