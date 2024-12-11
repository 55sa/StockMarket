package com.plcoding.stockmarketapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val DarkColorPalette = darkColors(
    primary = Color.Green,
    background = Color.Black,
    surface = Color.DarkGray,
    onPrimary = Color.White,
    onBackground = Color.White
)


private val LightColorPalette = lightColors(
    primary = Color.Green,
    background = Color.White,
    surface = Color.LightGray,
    onPrimary = Color.Black,
    onBackground = Color.Black
)

@Composable
fun StockMarketAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
