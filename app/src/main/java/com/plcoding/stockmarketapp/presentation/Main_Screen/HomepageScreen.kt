package com.plcoding.stockmarketapp.presentation.mainscreen

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
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun HomePageScreen(viewModel: HomeViewModel = hiltViewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCompany by remember { mutableStateOf<CompanyListing?>(null) }

    val watchlist by viewModel.watchlist.collectAsState()
    val intradayData by viewModel.nasdaqData.collectAsState()

    val randomCompany = (watchlist as? Resource.Success<List<CompanyListing>>)?.data?.randomOrNull()

    LaunchedEffect(randomCompany) {
        randomCompany?.let { viewModel.loadNasdaqData(it.symbol) }
    }

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
                textStyle = LocalTextStyle.current.copy(color = Color.White),
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

            // Random Stock Section
            randomCompany?.let { company ->
                Text(
                    text = company.name,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                when (intradayData) {
                    is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    is Resource.Success -> {
                        val data = (intradayData as Resource.Success<List<IntradayInfo>>).data
                        if (!data.isNullOrEmpty()) {
                            StockLineChart(
                                stockPrices = data.map { it.close },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        } else {
                            Text(
                                text = "No data available for ${company.name}",
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                    is Resource.Error -> Text(
                        text = "Failed to load data for ${company.name}",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Watchlist Section
            Text(
                text = if (searchQuery.isEmpty()) "Watchlist" else "Search Results",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn {
                val companies = (watchlist as? Resource.Success<List<CompanyListing>>)?.data ?: emptyList()

                val filteredCompanies = if (searchQuery.isEmpty()) companies else {
                    companies.filter {
                        it.name.contains(searchQuery, ignoreCase = true) || it.symbol.contains(searchQuery, ignoreCase = true)
                    }
                }

                items(filteredCompanies) { company ->
                    WatchlistItem(
                        name = company.name,
                        logoUrl = viewModel.getCompanyLogoUrl(company.symbol),
                        onClick = {
                            selectedCompany = company
                            viewModel.loadNasdaqData(company.symbol)
                        }
                    )
                }
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
fun WatchlistItem(name: String, logoUrl: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(logoUrl ?: ""),
            contentDescription = "$name logo",
            modifier = Modifier
                .size(40.dp)
                .padding(end = 8.dp)
        )
        Text(
            text = name,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StockLineChart(stockPrices: List<Double>, modifier: Modifier = Modifier) {
    val maxPrice = stockPrices.maxOrNull() ?: 1.0
    val minPrice = stockPrices.minOrNull() ?: 0.0

    Canvas(modifier = modifier) {
        val spacing = size.width / (stockPrices.size - 1)

        val path = Path().apply {
            stockPrices.forEachIndexed { index, price ->
                val x = index * spacing
                val y = size.height - ((price - minPrice) / (maxPrice - minPrice) * size.height).toFloat()
                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun BottomNavigationBar(modifier: Modifier) {
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Blue
    ) {
        BottomNavigationItem(
            icon = { Icon(painterResource(id = R.drawable.ic_home), contentDescription = "Home", modifier = Modifier.size(20.dp)) },
            selected = true,
            onClick = {}
        )
        BottomNavigationItem(
            icon = { Icon(painterResource(id = R.drawable.ic_folder), contentDescription = "Folder", modifier = Modifier.size(20.dp)) },
            selected = false,
            onClick = {}
        )
        BottomNavigationItem(
            icon = { Icon(painterResource(id = R.drawable.ic_notifications), contentDescription = "Notifications", modifier = Modifier.size(20.dp)) },
            selected = false,
            onClick = {}
        )
        BottomNavigationItem(
            icon = { Icon(painterResource(id = R.drawable.ic_profile), contentDescription = "Profile", modifier = Modifier.size(20.dp)) },
            selected = false,
            onClick = {}
        )
    }
}

@Destination
@Composable
fun HomeScreen() {
    HomePageScreen()
}


