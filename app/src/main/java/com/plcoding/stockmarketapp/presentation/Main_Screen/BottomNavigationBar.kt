package com.plcoding.stockmarketapp.presentation.Main_Screen

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.plcoding.stockmarketapp.presentation.destinations.HomePageScreenDestination
import com.plcoding.stockmarketapp.presentation.destinations.LoginAndSignUpScreenDestination
import com.plcoding.stockmarketapp.presentation.destinations.TestScreenDestination
import com.plcoding.stockmarketapp.presentation.destinations.TradingAnalysisScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun BottomNavigationBar(
    navigator: DestinationsNavigator,

    modifier: Modifier = Modifier
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.primary,
        modifier = modifier
    ) {
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
            selected = false, // Adjust this as needed to highlight selected item
            onClick = { navigator.navigate(HomePageScreenDestination) }
        )
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.ShowChart, contentDescription = "Analysis") },
            selected = false,
            onClick = {navigator.navigate(TradingAnalysisScreenDestination)}
        )
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications") },
            selected = false,
            onClick = {}
        )
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
            selected = false,
            onClick = {navigator.navigate(LoginAndSignUpScreenDestination)}
        )
    }
}