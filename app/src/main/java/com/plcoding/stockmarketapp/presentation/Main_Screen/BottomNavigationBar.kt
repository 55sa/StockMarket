package com.plcoding.stockmarketapp.presentation.Main_Screen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigation
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.stockmarketapp.presentation.destinations.HomePageScreenDestination
import com.plcoding.stockmarketapp.presentation.destinations.LoginAndSignUpScreenDestination
import com.plcoding.stockmarketapp.presentation.destinations.TradingAnalysisScreenDestination
import com.plcoding.stockmarketapp.ui.theme.DarkThemeColors
import com.plcoding.stockmarketapp.ui.theme.LightThemeColors
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Composable
fun BottomNavigationBar(
    navigator: DestinationsNavigator,
    ) {
    val isLargeScreen = LocalConfiguration.current.screenWidthDp > 600


    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

    var navigationBarHeight = 56.dp
    if (isLargeScreen){
        navigationBarHeight = 30.dp
    }

    BottomNavigation(
        backgroundColor = colorTheme.navigationBarColor,
        contentColor = colorTheme.primaryText,
        modifier = Modifier.height(navigationBarHeight).shadow(8.dp)
    ) {
        BottomNavigationItem(
            icon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            },
            selected = false,
            onClick = { navigator.navigate(HomePageScreenDestination) }
        )
        BottomNavigationItem(
            icon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.AutoMirrored.Filled.ShowChart,
                    contentDescription = "Analysis"
                )
            },
            selected = false,
            onClick = {navigator.navigate(TradingAnalysisScreenDestination)}
        )

        BottomNavigationItem(
            icon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile"
                )
            },
            selected = false,
            onClick = {navigator.navigate(LoginAndSignUpScreenDestination)}
        )
    }

}


