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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import com.plcoding.stockmarketapp.domain.model.WeeklyInfo
import com.plcoding.stockmarketapp.ui.theme.DarkThemeColors
import com.plcoding.stockmarketapp.ui.theme.LightThemeColors
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorPosition
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklyChart(
    infos: List<WeeklyInfo>,
    modifier: Modifier = Modifier
) {
    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

    val sortedInfos = infos.sortedBy { it.date }
    val closeValues = sortedInfos.map { it.close }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy")
    val currentDayLabel = sortedInfos.firstOrNull()?.date?.format(dateFormatter) ?: "Unknown Date"

    val dateFormatter2 = DateTimeFormatter.ofPattern("MM-dd")
    val dateLabels2 = sortedInfos.map { it.date.format(dateFormatter2) }

    // Define text color based on theme
    val textColor = colorTheme.primaryText

    // Define label text style
    val labelTextStyle = TextStyle(
        color = colorTheme.primaryText,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Italic,
        letterSpacing = 0.5.sp,
        lineHeight = 20.sp,
        fontFamily = FontFamily.SansSerif

    )

    // Determine line color based on price movement
    val lineColor = if (closeValues.isNotEmpty() && closeValues.last() > closeValues.first()) {
        colorTheme.analysisGreen // Price increased
    } else {
        colorTheme.analysisRed // Price decreased
    }

    // Define Y-axis max and min
    val maxVal = (closeValues.maxOrNull() ?: 0.0) + 1
    val minVal = (closeValues.minOrNull() ?: 0.0) - 1

    val axisProperties = GridProperties.AxisProperties(
        enabled = true,
        style = StrokeStyle.Dashed(intervals = floatArrayOf(10f, 10f)),
        color = SolidColor(lineColor),
        thickness = (0.5).dp,
        lineCount = 5
    )

    // Reduce X-axis label density
    val totalLabels = dateLabels2.size
    val desiredLabelCount = 6
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
            textStyle = TextStyle(
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                letterSpacing = 0.5.sp,
                lineHeight = 20.sp,
                fontFamily = FontFamily.SansSerif
            )
        ),
        // Indicator properties for Y-axis labels
        indicatorProperties = HorizontalIndicatorProperties(
            enabled = true,
            position = IndicatorPosition.Horizontal.Start, // Y-axis on the left
            textStyle = TextStyle(
                color = textColor,  // Y-axis text color
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                letterSpacing = 0.5.sp,
                lineHeight = 20.sp,
                fontFamily = FontFamily.SansSerif
            ),
        ),
        gridProperties = GridProperties(
            enabled = true,
            xAxisProperties = axisProperties,
            yAxisProperties = axisProperties
        ),
        // Customize the LabelHelper to change label colors based on the theme
        labelHelperProperties = LabelHelperProperties(
            textStyle = labelTextStyle
        ),
        animationMode = AnimationMode.Together(delayBuilder = { it * 500L })
    )
}
