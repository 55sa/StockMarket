package com.plcoding.stockmarketapp.presentation.mainscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.plcoding.stockmarketapp.R
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import com.plcoding.stockmarketapp.presentation.Main_Screen.HomeViewModel
import com.plcoding.stockmarketapp.presentation.company_info.StockChart
import com.plcoding.stockmarketapp.util.Resource
import com.ramcosta.composedestinations.annotation.Destination

@Destination(start = true)
@Composable
fun HomePageScreen(viewModel: HomeViewModel = hiltViewModel()) {
    var searchQuery by remember { mutableStateOf("") }

    val nasdaqData by viewModel.nasdaqData.collectAsState()
    val watchlist by viewModel.watchlist.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = "StockEasy",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Search Bar
            BasicTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                decorationBox = { innerTextField ->
                    Box {
                        if (searchQuery.isEmpty()) Text("Search", color = Color.Gray)
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nasdaq Chart Section
            Text(
                text = "TESLA",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            when (nasdaqData) {
                is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                is Resource.Success -> {
                    val data = (nasdaqData as Resource.Success<List<IntradayInfo>>).data
                    if (data != null) {
                        if (data.isNotEmpty()) {
                            StockChart(
                                infos = data,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        } else {
                            Text(
                                text = "No Nasdaq data available",
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
                is Resource.Error -> Text(
                    text = "Failed to load Nasdaq data",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Watchlist Section
            Text(
                text = "Watchlist",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            when (watchlist) {
                is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                is Resource.Success<*> -> {
                    val data = (watchlist as? Resource.Success<List<CompanyListing>>)?.data ?: emptyList()
                    LazyColumn {
                        items(data) { company ->
                            WatchlistItem(name = company.name)
                        }
                    }
                }
                is Resource.Error -> Text(
                    text = "Failed to load watchlist",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        // Bottom Navigation Bar
        BottomNavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}

@Composable
fun WatchlistItem(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun BottomNavigationBar(modifier: Modifier = Modifier) {
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Black,
        modifier = modifier
    ) {
        BottomNavigationItem(
            icon = { Icon(painterResource(id = R.drawable.ic_home), contentDescription = "Home", modifier = Modifier.size(25.dp)) },
            selected = true,
            onClick = {}
        )
        BottomNavigationItem(
            icon = { Icon(painterResource(id = R.drawable.ic_folder), contentDescription = "Folder", modifier = Modifier.size(25.dp)) },
            selected = false,
            onClick = {}
        )
        BottomNavigationItem(
            icon = { Icon(painterResource(id = R.drawable.ic_notifications), contentDescription = "Notifications", modifier = Modifier.size(25.dp)) },
            selected = false,
            onClick = {}
        )
        BottomNavigationItem(
            icon = { Icon(painterResource(id = R.drawable.ic_profile), contentDescription = "Profile", modifier = Modifier.size(25.dp)) },
            selected = false,
            onClick = {}
        )
    }
}
