package com.plcoding.stockmarketapp.presentation.mainscreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.IntradayInfo

import com.plcoding.stockmarketapp.presentation.Main_Screen.HomeViewModel
import com.plcoding.stockmarketapp.presentation.company_info.StockChart

import com.plcoding.stockmarketapp.presentation.company_listings.CompanyItem
import com.plcoding.stockmarketapp.presentation.destinations.CompanyInfoScreenDestination
import com.plcoding.stockmarketapp.presentation.destinations.CompanyListingsScreenDestination
import com.plcoding.stockmarketapp.presentation.destinations.HomePageScreenDestination

import com.plcoding.stockmarketapp.util.Resource
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Destination(start = true)
@Composable
fun HomePageScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }

    val watchlist by viewModel.watchlist.collectAsState()
    val intradayData by viewModel.nasdaqData.collectAsState()

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    // 每次打开页面时刷新数据
    LaunchedEffect(Unit) {
        viewModel.loadWatchlist()
        viewModel.loadNasdaqData()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = {
            BottomNavigationBar(
                onProfileClick = {  }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // 标题行和搜索按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f)) // 居中对齐
                Text(
                    text = "StockEasy",
                    style = MaterialTheme.typography.h4.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = 1.2.sp
                    ),
                    modifier = Modifier.weight(2f),
                    textAlign = TextAlign.Center
                )
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier
                        .size(24.dp)
                        .weight(1f)
                        .clickable {
                            navigator.navigate(CompanyListingsScreenDestination)
                        },
                    tint = MaterialTheme.colors.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 纳斯达克数据部分
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
                    val data = (intradayData as Resource.Success<List<IntradayInfo>>).data.orEmpty()
                    if (data.isNotEmpty()) {
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

            // Watchlist 部分
            val companies = (watchlist as? Resource.Success<List<CompanyListing>>)?.data.orEmpty()
            val filteredCompanies = if (searchQuery.isBlank()) {
                companies
            } else {
                companies.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                            it.symbol.contains(searchQuery, ignoreCase = true)
                }
            }

            Text(
                text = if (searchQuery.isBlank()) "Watchlist" else "Search Results",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn {
                items(
                    items = filteredCompanies,
                    key = { company -> company.symbol }
                ) { company ->
                    val dismissState = rememberDismissState(
                        confirmStateChange = {
                            if (it == DismissValue.DismissedToEnd) {
                                // 删除逻辑
                                scope.launch {
                                    viewModel.removeFromWatchlist(company.symbol)
                                    viewModel.loadWatchlist()
                                    scaffoldState.snackbarHostState.showSnackbar("${company.name} removed")
                                }
                            }
                            true
                        }
                    )

                    SwipeToDismiss(
                        state = dismissState,
                        background = {
                            // 增加动画和美观设计的背景
                            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
                            val alignment = if (direction == DismissDirection.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
                            val color by animateColorAsState(
                                targetValue = if (dismissState.targetValue == DismissValue.Default) Color.Gray else Color.Red
                            )
                            val scale by animateFloatAsState(
                                targetValue = if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = alignment
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White,
                                    modifier = Modifier.scale(scale)
                                )
                            }
                        },
                        directions = setOf(DismissDirection.StartToEnd)
                    ) {
                        CompanyItem(
                            company = company,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navigator.navigate(
                                        CompanyInfoScreenDestination(company.symbol)
                                    )
                                }
                                .padding(vertical = 8.dp)
                        )
                        Divider()
                    }
                }
            }
        }
    }
}


@Composable
fun BottomNavigationBar(
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.background, // 根据系统颜色模式动态变化
        contentColor = MaterialTheme.colors.primary, // 根据系统颜色变化
        modifier = modifier
    ) {
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
            selected = true,
            onClick = {}
        )
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.Folder, contentDescription = "Folder") },
            selected = false,
            onClick = {}
        )
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications") },
            selected = false,
            onClick = {}
        )
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
            selected = false,
            onClick = onProfileClick
        )
    }
}