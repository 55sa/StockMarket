package com.plcoding.stockmarketapp.presentation.trading_analysis

// Jetpack Compose
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable

import androidx.compose.material.Scaffold
import androidx.hilt.navigation.compose.hiltViewModel
import com.plcoding.stockmarketapp.presentation.Main_Screen.BottomNavigationBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun TradingAnalysisScreen(navigator: DestinationsNavigator,
                          viewModel: TradingAnalysisViewModel = hiltViewModel()
) {

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navigator = navigator
            )
        }
    ) { innerPadding ->
        Column{
            ChartScreen(viewModel = viewModel)
        }

    }
}
