package com.plcoding.stockmarketapp.presentation.company_info

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StockChart(
    infos: List<IntradayInfo> = emptyList(),
    modifier: Modifier = Modifier
) {
    val graphColor = if (infos.isNotEmpty() && infos.last().close >= infos.first().close) {
        Color.Green
    } else {
        Color.Red
    }

    val upperBound = (infos.maxOfOrNull { it.close }?.roundToInt() ?: 0) + 1
    val lowerBound = (infos.minOfOrNull { it.close }?.roundToInt() ?: 0) - 1

    Canvas(modifier = modifier) {
        val spacing = 20.dp.toPx()
        val canvasWidth = size.width
        val canvasHeight = size.height
        val pointSpacing = (canvasWidth - spacing) / infos.size

        // Draw horizontal grid lines and labels
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 12.sp.toPx()
        }
        val priceSteps = (upperBound - lowerBound) / 5
        for (i in 0..4) {
            val y = canvasHeight - (canvasHeight / 4f) * i
            drawLine(
                color = Color.Gray,
                start = Offset(spacing, y),
                end = Offset(canvasWidth, y),
                strokeWidth = 1.dp.toPx()
            )
            drawContext.canvas.nativeCanvas.drawText(
                (lowerBound + priceSteps * i).toString(),
                0f,
                y,
                textPaint
            )
        }

        // Create the graph path
        val graphPath = Path().apply {
            infos.forEachIndexed { index, info ->
                val x = spacing + index * pointSpacing
                val y = canvasHeight - ((info.close - lowerBound) / (upperBound - lowerBound) * (canvasHeight - spacing)).toFloat()
                if (index == 0) {
                    moveTo(x, y)
                } else {
                    lineTo(x, y)
                }
            }
        }

        // Create the fill path
        val fillPath = Path().apply {
            addPath(graphPath)
            lineTo(canvasWidth, canvasHeight)
            lineTo(spacing, canvasHeight)
            close()
        }

        // Draw the fill area
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(graphColor.copy(alpha = 0.4f), Color.Transparent),
                endY = canvasHeight
            )
        )

        // Draw the graph line
        drawPath(
            path = graphPath,
            color = graphColor,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
