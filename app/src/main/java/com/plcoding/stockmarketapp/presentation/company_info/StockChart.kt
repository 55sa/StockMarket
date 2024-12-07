package com.plcoding.stockmarketapp.presentation.company_info

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import java.time.format.DateTimeFormatter
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

    var selectedInfo by remember { mutableStateOf<IntradayInfo?>(null) }
    val dateFormatter = DateTimeFormatter.ofPattern("MM-dd HH:mm")
    val density = LocalDensity.current
    val textColor = MaterialTheme.colors.onBackground

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp) // Prevent content from touching screen edges
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(300.dp) // Fixed height for the chart area
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        with(density) {
                            val spacing = 20.dp.toPx()
                            val canvasWidth = size.width
                            val pointSpacing = (canvasWidth - spacing) / (infos.size - 1)

                            val tappedIndex = ((offset.x - spacing) / pointSpacing).roundToInt()
                                .coerceIn(0, infos.size - 1)

                            val tappedPointX = spacing + tappedIndex * pointSpacing
                            val tappedPointY = size.height - ((infos[tappedIndex].close - lowerBound) / (upperBound - lowerBound) * size.height)
                            val touchThreshold = 20.dp.toPx()

                            if (offset.x in (tappedPointX - touchThreshold)..(tappedPointX + touchThreshold) &&
                                offset.y in (tappedPointY - touchThreshold)..(tappedPointY + touchThreshold)) {
                                selectedInfo = infos.getOrNull(tappedIndex)
                            } else {
                                selectedInfo = null
                            }
                        }
                    }
                }
        ) {
            with(density) {
                val spacing = 20.dp.toPx()
                val canvasWidth = size.width
                val canvasHeight = size.height
                val pointSpacing = (canvasWidth - spacing) / (infos.size - 1)

                // Draw horizontal grid lines and labels
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
                        "${(lowerBound + priceSteps * i)}",
                        spacing / 2,
                        y,
                        android.graphics.Paint().apply {
                            color = textColor.toArgb()
                            textSize = 12.sp.toPx()
                            textAlign = android.graphics.Paint.Align.RIGHT
                        }
                    )
                }

                // Draw vertical grid lines and labels
                infos.forEachIndexed { index, info ->
                    val x = spacing + index * pointSpacing
                    if (index % 5 == 0 || index == infos.size - 1) {
                        val formattedDate = info.date.format(dateFormatter)
                        drawLine(
                            color = Color.Gray,
                            start = Offset(x, 0f),
                            end = Offset(x, canvasHeight),
                            strokeWidth = 0.5.dp.toPx()
                        )
                        drawContext.canvas.nativeCanvas.drawText(
                            formattedDate,
                            x,
                            canvasHeight + 15.dp.toPx(),
                            android.graphics.Paint().apply {
                                color = textColor.toArgb()
                                textSize = 12.sp.toPx()
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                        )
                    }
                }

                // Create graph path
                val graphPath = Path().apply {
                    infos.forEachIndexed { index, info ->
                        val x = spacing + index * pointSpacing
                        val y = canvasHeight - ((info.close - lowerBound) / (upperBound - lowerBound) * canvasHeight)
                        if (index == 0) moveTo(x, y.toFloat()) else lineTo(x, y.toFloat())
                    }
                }

                // Draw graph path
                drawPath(
                    path = graphPath,
                    color = graphColor,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw points
                infos.forEachIndexed { index, info ->
                    val x = spacing + index * pointSpacing
                    val y = canvasHeight - ((info.close - lowerBound) / (upperBound - lowerBound) * canvasHeight)
                    drawCircle(
                        color = graphColor,
                        center = Offset(x, y.toFloat()),
                        radius = 4.dp.toPx()
                    )
                }
            }
        }

        // Tooltip at the bottom
        selectedInfo?.let { info ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Date: ${info.date.format(dateFormatter)}\nPrice: ${String.format("%.2f", info.close)}",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}




