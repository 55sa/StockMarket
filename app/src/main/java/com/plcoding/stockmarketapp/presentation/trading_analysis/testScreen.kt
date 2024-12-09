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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.http.Url
import java.io.InputStream

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
    val content = remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(url) {
        scope.launch {
            val inputStream = fetchStreamFromUrl(url)
            inputStream?.let {
                val text = it.bufferedReader().use { reader -> reader.readText() }
                content.value = text
            } ?: run {
                content.value = "Failed to load content."
            }
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = content.value ?: "Loading...",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )
    }
}


suspend fun fetchStreamFromUrl(url: String): InputStream? = withContext(Dispatchers.IO) {
    val client = OkHttpClient()

    val request = Request.Builder()
        .url(url)
        .build()

    val response = client.newCall(request).execute()

    if (!response.isSuccessful) {
        println("Failed to fetch stream: ${response.code}")
        return@withContext null
    }

    response.body?.byteStream()
}