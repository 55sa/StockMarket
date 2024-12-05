package com.plcoding.stockmarketapp.presentation.company_info

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.plcoding.stockmarketapp.R
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.presentation.destinations.CompanyListingsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

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
                    .padding(16.dp)
            ) {

                // Top Row with Back and Add to Watchlist Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_revert),
                            contentDescription = "Back",
                            tint = MaterialTheme.colors.primary
                        )
                    }

                    if (!state.isInWatchList) {
                        IconButton(onClick = { viewModel.addToWatchList(symbol) }) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_add),
                                contentDescription = "Add to Watchlist",
                                tint = MaterialTheme.colors.primary
                            )
                        }
                    }
                }

                // Company Details
                state.company?.let { company ->
                    Card(
                        elevation = 4.dp,
                        shape = MaterialTheme.shapes.medium,
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
                                    letterSpacing = 1.2.sp
                                ),
                                color = MaterialTheme.colors.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = company.symbol,
                                style = MaterialTheme.typography.subtitle1.copy(
                                    fontStyle = FontStyle.Italic,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = MaterialTheme.colors.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Industry: ${company.industry}",
                                style = MaterialTheme.typography.body1.copy(
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colors.onSurface
                            )
                            Text(
                                text = "Country: ${company.country}",
                                style = MaterialTheme.typography.body1.copy(
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colors.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = company.description,
                                style = MaterialTheme.typography.body2.copy(
                                    fontFamily = FontFamily.Serif,
                                    lineHeight = 20.sp
                                ),
                                color = MaterialTheme.colors.onSurface
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
                            fontFamily = FontFamily.Cursive
                        ),
                        color = MaterialTheme.colors.primary
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

                // GPT Analysis
                state.gptmesg?.let { gptMessage ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "AI Buffett Analysis:",
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                        color = MaterialTheme.colors.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        elevation = 4.dp,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = gptMessage,
                            style = MaterialTheme.typography.body2.copy(
                                fontFamily = FontFamily.Cursive,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colors.onSurface,
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
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
