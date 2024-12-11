package com.plcoding.stockmarketapp.presentation.company_info

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.plcoding.stockmarketapp.R
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.presentation.destinations.CompanyListingsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Composable
fun StyledIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    iconTint: Color
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun StockDetailItem(label: String, value: Double) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .width(150.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.body2.copy(
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Italic,
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
        )
    }
}

@Composable
fun IntradayInfoCard(intradayInfo: IntradayInfo) {
    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Latest Stock Data",
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colors.primary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StockDetailItem(label = "Close", value = intradayInfo.close)
                StockDetailItem(label = "Volume", value = intradayInfo.volume)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StockDetailItem(label = "Low", value = intradayInfo.low)
                StockDetailItem(label = "High", value = intradayInfo.high)
            }
        }}}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Destination
fun CompanyInfoScreen(
    symbol: String,
    navigator: DestinationsNavigator,
    viewModel: CompanyInfoViewModel = hiltViewModel()
) {
    val state = viewModel.state

    LaunchedEffect(Unit) {
        viewModel.checkIfInWatchlist(symbol)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        if (state.error == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Top Row with Back and Add to Watchlist Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 返回按钮
                    StyledIconButton(
                        icon = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        onClick = { navigator.popBackStack() },
                        backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                        iconTint = MaterialTheme.colors.primary
                    )

                    // 添加到观察列表按钮
                    if (!state.isInWatchList) {
                        StyledIconButton(
                            icon = Icons.Default.Add,
                            contentDescription = "Add to Watchlist",
                            onClick = { viewModel.addToWatchList(symbol) },
                            backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                            iconTint = MaterialTheme.colors.primary
                        )
                    } else {
                        // 已添加到观察列表时显示一个确认图标
                        StyledIconButton(
                            icon = Icons.Default.Check,
                            contentDescription = "In Watchlist",
                            onClick = {  },
                            backgroundColor = MaterialTheme.colors.secondary.copy(alpha = 0.1f),
                            iconTint = MaterialTheme.colors.secondary
                        )
                    }
                }

                // Company Details
                state.company?.let { company ->
                    Card(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(12.dp),
                        backgroundColor = MaterialTheme.colors.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = company.name,
                                style = MaterialTheme.typography.h5.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    letterSpacing = 1.2.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = company.symbol,
                                style = MaterialTheme.typography.subtitle1.copy(
                                    fontStyle = FontStyle.Italic,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Industry: ${company.industry}",
                                style = MaterialTheme.typography.body1.copy(
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Medium,
                                    fontStyle = FontStyle.Italic,
                                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                                )
                            )
                            Text(
                                text = "Country: ${company.country}",
                                style = MaterialTheme.typography.body1.copy(
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Medium,
                                    fontStyle = FontStyle.Italic,
                                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = company.description,
                                style = MaterialTheme.typography.body2.copy(
                                    fontFamily = FontFamily.Serif,
                                    lineHeight = 20.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                                ),
                                maxLines = 10,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Stock Chart
                if (state.stockInfos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Market Summary",
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colors.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    StockChart(
                        infos = state.stockInfos,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(horizontal = 8.dp)
                    )
                }

                // Additional Stock Information
                if (state.stockInfos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Stock Details",
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colors.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Display all IntradayInfo items

                        IntradayInfoCard(intradayInfo = state.stockInfos.get(0))
                        Spacer(modifier = Modifier.height(8.dp))

                }

                // GPT Analysis
                state.gptmesg?.let { gptMessage ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "AI Buffett Analysis:",
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = MaterialTheme.colors.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(12.dp),
                        backgroundColor = MaterialTheme.colors.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = gptMessage,
                            style = MaterialTheme.typography.body2.copy(
                                fontWeight = FontWeight.Medium,
                                fontStyle = FontStyle.Italic,
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black
                            ),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }

        // Loading State
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        // Error State
        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.body1.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }






}



