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

    val navigationBarColor: Color
)

// Light Mode 颜色
val LightThemeColors = ColorTheme(
    screenBackgroundColor = Color(0xFFFDFDFD),
    background = Color(0xFFF3EBDD),
    cardBackground = Color(0xFFEEE0BA),
    primaryText = Color(0xFF000000),
    secondaryText = Color(0xFF444444),
    shadow = Color(0xFFFDF6ED), // shadow,
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

    navigationBarColor = Color(0xFFF3EBDD)
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

    navigationBarColor = Color(0xFF1C1C2A)
)

val DarkModeTopBarColors = listOf(
    Color(0xFF1F1F1F), // 纯深灰色，经典暗黑风格
    Color(0xFF2C2C2C), // 略浅的深灰色，增加一丝柔和感
    Color(0xFF37474F), // 深灰蓝色，带有现代感
    Color(0xFF263238), // 蓝灰色，适合专业场景
    Color(0xFF1A237E), // 深靛蓝色，增加冷色调深度
    Color(0xFF4A4A4A), // 炭灰色，低调但有质感
    Color(0xFF2E3B4E), // 深蓝灰色，带有稳定感
    Color(0xFF202830), // 深灰黑，轻微偏冷
    Color(0xFF3C4043), // 石墨灰，现代感十足
    Color(0xFF282828)  // 冷调深灰色，精致稳重
)

val LightModeTopBarColors = listOf(
    Color(0xFFFFFFFF), // 纯白色，简洁清新
    Color(0xFFF5F5F5), // 极浅灰色，柔和明亮
    Color(0xFFE8F0FE), // 浅蓝白色，带有冷色调
    Color(0xFFFFFBF2), // 米黄色白，温暖舒适
    Color(0xFFFFF9C4), // 柔和浅黄色，活力温暖
    Color(0xFFE0F7FA), // 极浅青色，清爽自然
    Color(0xFFECEFF1), // 雾灰色，现代简约
    Color(0xFFE8EAF6), // 淡紫灰色，优雅柔和
    Color(0xFFFFF3E0), // 浅橙色，温馨且不刺眼
    Color(0xFFD7CCC8)  // 浅棕灰色，低调典雅
)
