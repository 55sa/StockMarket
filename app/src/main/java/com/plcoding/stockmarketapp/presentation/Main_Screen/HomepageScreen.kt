package com.plcoding.stockmarketapp.presentation.mainscreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.plcoding.stockmarketapp.R
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import com.plcoding.stockmarketapp.presentation.Main_Screen.HomeViewModel
import com.plcoding.stockmarketapp.util.Resource
import androidx.compose.ui.graphics.Path
import com.plcoding.stockmarketapp.presentation.company_info.StockChart
import com.plcoding.stockmarketapp.presentation.company_listings.CompanyItem
import com.ramcosta.composedestinations.annotation.Destination

@RequiresApi(Build.VERSION_CODES.O)
@Destination(start = true)
@Composable
fun HomePageScreen(viewModel: HomeViewModel = hiltViewModel()) {
    var searchQuery by remember { mutableStateOf("") }

    val watchlist by viewModel.watchlist.collectAsState()
    val intradayData by viewModel.nasdaqData.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar()
        }
    ) { innerPadding ->
        // Content inside the Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search...") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nasdaq Data Section
            Text(
                text = "Nasdaq Data",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            when (intradayData) {
                is Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is Resource.Success -> {
                    val data = (intradayData as Resource.Success<List<IntradayInfo>>).data
                    if (!data.isNullOrEmpty()) {
                        StockChart(
                            infos = data,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    } else {
                        Text(
                            text = "No data available for Nasdaq",
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
                is Resource.Error -> {
                    Text(
                        text = "Failed to load Nasdaq data",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Watchlist Section
            val companies = (watchlist as? Resource.Success<List<CompanyListing>>)?.data.orEmpty()
            val filteredCompanies = if (searchQuery.isBlank()) companies else {
                companies.filter { it.name.contains(searchQuery, ignoreCase = true) || it.symbol.contains(searchQuery, ignoreCase = true) }
            }

            Text(
                text = if (searchQuery.isBlank()) "Watchlist" else "Search Results",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn {
                items(filteredCompanies) { company ->
                    CompanyItem(
                        company = company,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* Handle navigation */ }
                            .padding(vertical = 8.dp)
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(modifier: Modifier = Modifier) {
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Green
    ) {
        BottomNavigationItem(
            icon = { Icon(painter = painterResource(id = R.drawable.ic_home), contentDescription = "Home") },
            selected = true,
            onClick = {}
        )
        BottomNavigationItem(
            icon = { Icon(painter = painterResource(id = R.drawable.ic_folder), contentDescription = "Folder") },
            selected = false,
            onClick = {}
        )
        BottomNavigationItem(
            icon = { Icon(painter = painterResource(id = R.drawable.ic_notifications), contentDescription = "Notifications") },
            selected = false,
            onClick = {}
        )
        BottomNavigationItem(
            icon = { Icon(painter = painterResource(id = R.drawable.ic_profile), contentDescription = "Profile") },
            selected = false,
            onClick = {}
        )
    }
}
