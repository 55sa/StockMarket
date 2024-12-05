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
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.presentation.Login.AuthViewModel
import com.plcoding.stockmarketapp.presentation.Main_Screen.BottomNavigationBar
import com.plcoding.stockmarketapp.presentation.Main_Screen.HomeViewModel
import com.plcoding.stockmarketapp.presentation.destinations.LoginAndSignUpScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import retrofit2.http.Url

@Destination
@Composable
fun TestScreen(
    navigator: DestinationsNavigator,
    authViewModel: AuthViewModel = hiltViewModel(),
    testViewModel: TestViewModel = hiltViewModel()
) {
    val state by authViewModel.state.collectAsState()
    val userFileUrl by testViewModel.userFileUrl.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navigator = navigator
            )
        }
    ) { innerPadding ->
        if (!state.isLoggedIn) {
            // Redirect to login if user is not logged in
            LaunchedEffect(Unit) {
                navigator.navigate(LoginAndSignUpScreenDestination)
            }
        } else {
            // Trigger fetching the file URL once the user ID is available
            LaunchedEffect(state.userId) {
                state.userId?.let {
                    testViewModel.fetchUserFileUrl(it)
                }
            }

            when {
                userFileUrl == null -> {
                    // Loading or error state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Loading user data...")
                    }
                }
                else -> {
                    // Show content when the URL is loaded
                    SuccessfulPageContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        url = userFileUrl ?: "Error loading file URL"
                    )
                }
            }
        }
    }
}

@Composable
fun SuccessfulPageContent(modifier: Modifier = Modifier, url: String) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = url,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )
    }
}
