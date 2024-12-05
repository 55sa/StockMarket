package com.plcoding.stockmarketapp.presentation.trading_analysis

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.plcoding.stockmarketapp.presentation.Login.AuthViewModel
import com.plcoding.stockmarketapp.presentation.Main_Screen.BottomNavigationBar
import com.plcoding.stockmarketapp.presentation.Main_Screen.HomeViewModel
import com.plcoding.stockmarketapp.presentation.destinations.LoginAndSignUpScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@SuppressLint("StateFlowValueCalledInComposition")
@Destination
@Composable
fun TestScreen(
    navigator: DestinationsNavigator,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val state by authViewModel.state.collectAsState()

    LaunchedEffect(state.isLoggedIn) {

    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navigator = navigator
            )
        }
    ) { innerPadding ->
        if (state.isLoggedIn) {
            SuccessfulPageContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            // Placeholder for unauthenticated state until redirection happens
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Redirecting to Login...")
            }
        }
    }
}

@Composable
fun SuccessfulPageContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to the Trading Analysis Page!",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )
    }
}