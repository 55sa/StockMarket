package com.plcoding.stockmarketapp.presentation.trading_analysis

// Jetpack Compose
import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.plcoding.stockmarketapp.presentation.Login.AuthViewModel
import com.plcoding.stockmarketapp.presentation.Main_Screen.BottomNavigationBar
import com.plcoding.stockmarketapp.presentation.Main_Screen.FloatingTitle
import com.plcoding.stockmarketapp.presentation.destinations.LoginAndSignUpScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun TradingAnalysisScreen(
    navigator: DestinationsNavigator,
    viewModel: TradingAnalysisViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // 收集认证状态
    val authState by authViewModel.state.collectAsState()
    val scaffoldState = rememberScaffoldState()

    if (!authState.isLoggedIn) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { FloatingTitle() },
            bottomBar = {
                BottomNavigationBar(navigator = navigator)
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Please Login to Access Trading Analysis",
                        style = MaterialTheme.typography.body2.copy(
                            fontWeight = FontWeight.Medium,
                            fontStyle = FontStyle.Italic,
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black

                        ),

                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = { navigator.navigate(LoginAndSignUpScreenDestination) }) {
                        Text("Login")
                    }
                }
            }
        }
    } else {
        if(LocalConfiguration.current.screenWidthDp > 600){
            Scaffold(

                bottomBar = {
                    BottomNavigationBar(
                        navigator = navigator
                    )
                }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    ChartScreen(viewModel = viewModel)
                }
            }
        }else{

        Scaffold(
            topBar = { FloatingTitle() },
            bottomBar = {
                BottomNavigationBar(
                    navigator = navigator
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                ChartScreen(viewModel = viewModel)
            }
        }
    }}
}