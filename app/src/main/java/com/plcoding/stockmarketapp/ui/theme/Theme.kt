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


data class ColorTheme(
    val screenBackgroundColor: Color,
    val background: Color,
    val cardBackground: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val shadow: Color,
    val divider: Color,

    val chartColorDefault: List<Color>,
    val chartGradientColorSet1: List<Color>,
    val chartGradientColorSet2: List<Color>,
    val chartGradientColorSet3: List<Color>,
    val chartGradientFillColor1: Color,
    val chartGradientFillColor2: Color,
    val chartGradientFillColor3: Color,

    val analysisGreen: Color,
    val analysisRed: Color,
)

// Light Mode 颜色
val LightThemeColors = ColorTheme(
    screenBackgroundColor = Color(0xFF37474F),
    background = Color(0xFFF5E8C7),
    cardBackground = Color(0xFFDAD7D7),
    primaryText = Color(0xFF000000),
    secondaryText = Color(0xFF444444),
    shadow = Color.White, // shadow,
    divider = Color(0xFF444444),

    chartColorDefault = listOf(Color.Gray, Color.DarkGray),


    chartGradientColorSet1 = listOf(Color(0xFF9D6718), Color(0xFFA13818)),
    chartGradientColorSet2 = listOf(Color(0xFF5F9DCB), Color(0xFF2B578C)),
    chartGradientColorSet3 = listOf(Color(0xFFB24BA5), Color(0xFF531C59)),

    chartGradientFillColor1 = Color(0xFFFFA726),
    chartGradientFillColor2 = Color(0xFF5F9DCB),
    chartGradientFillColor3 = Color(0xFFB24BA5),

    analysisGreen = Color(0xFF3CA438),
    analysisRed = Color(0xFFD32C2C),


    )

// Dark Mode 颜色
val DarkThemeColors = ColorTheme(
    screenBackgroundColor = Color.Black,
    background = Color(0xFF1C1C2A), // 深灰色背景
    cardBackground = Color(0xFF2A2A3A), // 深灰色卡片背景
    primaryText = Color(0xFFFFFFFF), // 白色主要文字
    secondaryText = Color(0xFFAAAAAA), // 浅灰色次要文字
    shadow = Color.Black, // shadow
    divider = Color(0xFF444444), // 分割线深灰色

    chartColorDefault = listOf(Color.Gray, Color.DarkGray),
    chartGradientColorSet1 = listOf(Color(0xFFFFA726), Color(0xFFFF5722)),
    chartGradientColorSet2 = listOf(Color(0xFF3AB3E8), Color(0xFF0056A6)),
    chartGradientColorSet3 = listOf(Color(0xFF76C7C0), Color(0xFF2A9D8F)),

    chartGradientFillColor1 = Color(0xFFFFA726),
    chartGradientFillColor2= Color(0xFF3AB3E8),
    chartGradientFillColor3 = Color(0xFF76C7C0),

    analysisGreen = Color.Green,
    analysisRed = Color.Red,
)