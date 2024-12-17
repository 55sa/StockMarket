package com.plcoding.stockmarketapp.presentation.Main_Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.stockmarketapp.ui.theme.DarkThemeColors
import com.plcoding.stockmarketapp.ui.theme.LightThemeColors
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingTitle(
    modifier: Modifier = Modifier,
    title: String = "StockEasy"
) {
    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

    CenterAlignedTopAppBar(
        modifier = Modifier.shadow(8.dp),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = colorTheme.navigationBarColor,
            titleContentColor = colorTheme.primaryText,
        ),
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                letterSpacing = 1.2.sp,
                fontSize = 28.sp, // Slightly reduced font size
                color = colorTheme.primaryText // Dynamic color based on theme
            )
        },


    )
}
