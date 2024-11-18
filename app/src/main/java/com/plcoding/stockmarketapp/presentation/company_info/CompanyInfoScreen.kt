package com.plcoding.stockmarketapp.presentation.company_info

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.plcoding.stockmarketapp.ui.theme.DarkBlue
import com.ramcosta.composedestinations.annotation.Destination
@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Destination
fun CompanyInfoScreen(
    symbol: String,
    viewModel: CompanyInfoViewModel = hiltViewModel()
) {
    val state = viewModel.state

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
                state.company?.let { company ->
                    Text(
                        text = company.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = company.symbol,
                        fontStyle = FontStyle.Italic,
                        fontSize = 14.sp,
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Industry: ${company.industry}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier.fillMaxWidth(),
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Country: ${company.country}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier.fillMaxWidth(),
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = company.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    if (state.stockInfos.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Market Summary",
                            color = MaterialTheme.colors.onBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        StockChart(
                            infos = state.stockInfos,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }

                    state.gptmesg?.let { gptMessage ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "GPT Analysis",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colors.onBackground,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = gptMessage,
                            fontSize = 14.sp,
                            color = MaterialTheme.colors.onBackground,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Center))
        } else if (state.error != null) {
            Text(
                text = state.error,
                color = MaterialTheme.colors.error,
                modifier = Modifier.align(Center)
            )
        }
    }
}