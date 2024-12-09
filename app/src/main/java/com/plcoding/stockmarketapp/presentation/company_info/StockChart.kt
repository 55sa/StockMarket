package com.plcoding.stockmarketapp.presentation.company_info

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StockChart(
    infos: List<IntradayInfo>,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()

    val sortedInfos = infos.sortedBy { it.date }
    val closeValues = sortedInfos.map { it.close }

    val dateFormatter = DateTimeFormatter.ofPattern("MM/dd")
    val currentDayLabel = sortedInfos.firstOrNull()?.date?.format(dateFormatter) ?: "Unknown Date"

    val dateFormatter2 = DateTimeFormatter.ofPattern("HH:mm")
    val dateLabels2 = sortedInfos.map { it.date.format(dateFormatter2) }

    // 根据主题决定文字颜色
    val textColor = if (isDarkTheme) {
        Color.Green // DarkMode下数字为绿色
    } else {
        Color.Black // LightMode下为黑色或可根据需要调整
    }

    // 根据涨跌情况决定线的颜色
    val lineColor = if (closeValues.isNotEmpty() && closeValues.last() > closeValues.first()) {
        Color.Green // 涨
    } else {
        Color.Red // 跌
    }

    // 定义Y轴最大最小值
    val maxVal = (closeValues.maxOrNull() ?: 0.0) + 1
    val minVal = (closeValues.minOrNull() ?: 0.0) - 1

    val axisProperties = GridProperties.AxisProperties(
        enabled = true,
        style = StrokeStyle.Dashed(intervals = floatArrayOf(10f, 10f)),
        color = SolidColor(lineColor),
        thickness = (0.5).dp,
        lineCount = 5
    )

    // 抽样减少X轴标签密度
    val totalLabels = dateLabels2.size
    val desiredLabelCount = 6 // 期望的标签数，可根据需要调整
    val step = (totalLabels / desiredLabelCount).coerceAtLeast(1)
    val reducedLabels = dateLabels2.filterIndexed { index, _ ->
        index % step == 0
    }

    LineChart(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        data = remember {
            listOf(
                Line(
                    label = currentDayLabel,
                    values = closeValues,
                    color = SolidColor(lineColor),
                    firstGradientFillColor = lineColor.copy(alpha = 0.5f),
                    secondGradientFillColor = Color.Transparent,
                    strokeAnimationSpec = tween(durationMillis = 2000, easing = EaseInOutCubic),
                    gradientAnimationDelay = 1000,
                    drawStyle = DrawStyle.Stroke(width = 2.dp)
                )
            )
        },
        maxValue = maxVal,
        minValue = minVal,
        labelProperties = LabelProperties(
            enabled = true,
            labels = reducedLabels,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = textColor,
                fontSize = 12.sp
            )
        ),
        gridProperties = GridProperties(
            enabled = true,
            xAxisProperties = axisProperties,
            yAxisProperties = axisProperties
        ),
        animationMode = AnimationMode.Together(delayBuilder = { it * 500L })
    )
}