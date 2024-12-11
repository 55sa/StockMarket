package com.plcoding.stockmarketapp.presentation.mainscreen

import android.annotation.SuppressLint
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
import com.plcoding.stockmarketapp.presentation.Login.AuthViewModel
import com.plcoding.stockmarketapp.presentation.Main_Screen.BottomNavigationBar


import com.plcoding.stockmarketapp.presentation.Main_Screen.HomeViewModel
import com.plcoding.stockmarketapp.presentation.company_info.StockChart

import com.plcoding.stockmarketapp.presentation.company_listings.CompanyItem

import com.plcoding.stockmarketapp.presentation.destinations.CompanyInfoScreenDestination
import com.plcoding.stockmarketapp.presentation.destinations.CompanyListingsScreenDestination
import com.plcoding.stockmarketapp.presentation.destinations.HomePageScreenDestination
import com.plcoding.stockmarketapp.presentation.destinations.LoginAndSignUpScreenDestination
import com.plcoding.stockmarketapp.util.Resource
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
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

    LaunchedEffect(Unit) {
        viewModel.loadWatchlist()
        viewModel.loadNasdaqData()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = {
            BottomNavigationBar(navigator = navigator)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // 标题栏
            item {
                Floating(
                    title = "StockEasy",
                    onSearchClick = {
                        navigator.navigate(CompanyListingsScreenDestination)
                    }
                )
            }

            // 公司名称
            item {
                Text(
                    text = "TESLA",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }



            // 图表
            item {
                when (intradayData) {
                    is Resource.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    is Resource.Error -> {
                        Text(
                            text = "Failed to load Nasdaq data",
                            color = Color.Red,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }



            // 搜索结果或观察列表标题
            item {
                Text(
                    text = if (searchQuery.isBlank()) "Watchlist" else "Search Results",
                    style = MaterialTheme.typography.body2.copy(
                        fontSize = 20.sp, // 增大字体大小
                        fontWeight = FontWeight.Medium,
                        fontStyle = FontStyle.Italic,
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black
                    ),
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // 公司列表
            items(
                items = (watchlist as? Resource.Success<List<CompanyListing>>)?.data.orEmpty().let { companies ->
                    if (searchQuery.isBlank()) {
                        companies
                    } else {
                        companies.filter {
                            it.name.contains(searchQuery, ignoreCase = true) ||
                                    it.symbol.contains(searchQuery, ignoreCase = true)
                        }
                    }
                },
                key = { company -> company.symbol }
            ) { company ->
                val dismissState = rememberDismissState(
                    confirmStateChange = {
                        if (it == DismissValue.DismissedToEnd) {
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
                    Column {
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
fun Floating(
    title: String = "StockEasy",
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 判断系统是否处于深色主题
    val isDarkTheme = isSystemInDarkTheme()

    TopAppBar(
        backgroundColor = Color.Transparent, // 透明背景实现浮动效果
        elevation = 0.dp, // 无阴影
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 标题文本，居中对齐
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                letterSpacing = 1.2.sp,
                fontSize = 28.sp, // 调整字体大小
                color = if (isDarkTheme) Color.White else Color.Black,
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center
            )
            // 搜索图标，对齐到右侧
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterEnd)
                    .clickable { onSearchClick() }, // 点击事件回调
                tint = MaterialTheme.colors.primary
            )
        }
    }
}
