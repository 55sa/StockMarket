package com.plcoding.stockmarketapp.presentation.Main_Screen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FloatingTitle(
    title: String = "StockEasy",
    modifier: Modifier = Modifier
) {
    // Determine if the system is in dark theme
    val isDarkTheme = isSystemInDarkTheme()

    TopAppBar(
        backgroundColor = Color.Transparent, // Transparent background for floating effect
        elevation = 0.dp, // No elevation
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()

        ) {

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                letterSpacing = 1.2.sp,
                fontSize = 28.sp, // Slightly reduced font size
                color = if (isDarkTheme) Color.White else Color.Black // Dynamic color based on theme
            )
        }
    }
}