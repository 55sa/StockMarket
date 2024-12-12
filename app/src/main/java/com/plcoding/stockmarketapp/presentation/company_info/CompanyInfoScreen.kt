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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import com.plcoding.stockmarketapp.domain.model.MonthlyInfo
import com.plcoding.stockmarketapp.domain.model.WeeklyInfo
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
                text = "Latest Intraday Data",
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
        }
    }
}

@Composable
fun WeeklyInfoCard(weeklyInfo: WeeklyInfo) {
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
                text = "Latest Weekly Data",
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
                StockDetailItem(label = "Close", value = weeklyInfo.close)
                StockDetailItem(label = "Volume", value = weeklyInfo.volume)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StockDetailItem(label = "Low", value = weeklyInfo.low)
                StockDetailItem(label = "High", value = weeklyInfo.high)
            }
        }
    }
}

@Composable
fun MonthlyInfoCard(monthlyInfo: MonthlyInfo) {
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
                text = "Latest Monthly Data",
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
                StockDetailItem(label = "Close", value = monthlyInfo.close)
                StockDetailItem(label = "Volume", value = monthlyInfo.volume)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StockDetailItem(label = "Low", value = monthlyInfo.low)
                StockDetailItem(label = "High", value = monthlyInfo.high)
            }
        }
    }
}

@Composable
fun ChartTypeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .height(40.dp)
            .clickable(onClick = onClick),
        color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
        shape = RoundedCornerShape(20.dp),
        elevation = if (isSelected) 8.dp else 2.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = text,
                color = if (isSelected) Color.White else MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.button.copy(
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic
                )
            )
        }
    }
}

@Composable
fun ChartTypeSelector(
    selectedChartType: ChartType,
    onSelectChartType: (ChartType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Intraday Button
        ChartTypeButton(
            text = "Intraday",
            isSelected = selectedChartType == ChartType.INTRADAY,
            onClick = { onSelectChartType(ChartType.INTRADAY) }
        )
        // Weekly Button
        ChartTypeButton(
            text = "Weekly",
            isSelected = selectedChartType == ChartType.WEEKLY,
            onClick = { onSelectChartType(ChartType.WEEKLY) }
        )
        // Monthly Button
        ChartTypeButton(
            text = "Monthly",
            isSelected = selectedChartType == ChartType.MONTHLY,
            onClick = { onSelectChartType(ChartType.MONTHLY) }
        )
    }
}

@Composable
fun CompanyDetailsCard(company: CompanyInfo) {
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

@Composable
fun GPTAnalysisCard(gptMessage: String) {
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

@Composable
fun ErrorMessage(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colors.error,
        style = MaterialTheme.typography.body1.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
        ),

    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IntradaySection(intradayInfos: List<IntradayInfo>) {
    if (intradayInfos.isNotEmpty()) {
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
            infos = intradayInfos,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(horizontal = 8.dp)
        )

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
        // Display the latest IntradayInfo item
        IntradayInfoCard(intradayInfo = intradayInfos.last())
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklySection(weekInfos: List<WeeklyInfo>) {
    if (weekInfos.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Weekly Summary",
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colors.primary
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        WeeklyChart(
            infos = weekInfos,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Weekly Stock Details",
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colors.primary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Display the latest WeeklyInfo item
        WeeklyInfoCard(weeklyInfo = weekInfos.last())
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthlySection(monthInfos: List<MonthlyInfo>) {
    if (monthInfos.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Monthly Summary",
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colors.primary
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        MonthlyChart(
            infos = monthInfos,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Monthly Stock Details",
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colors.primary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Display the latest MonthlyInfo item
        MonthlyInfoCard(monthlyInfo = monthInfos.last())
        Spacer(modifier = Modifier.height(8.dp))
    }
}






@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Destination
fun CompanyInfoScreen(
    symbol: String,
    navigator: DestinationsNavigator,
    viewModel: CompanyInfoViewModel = hiltViewModel()
) {
    val state = viewModel.state
    var selectedChartType by rememberSaveable { mutableStateOf(ChartType.INTRADAY) }

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
                            onClick = { /* 可选：显示已添加到观察列表的反馈 */ },
                            backgroundColor = MaterialTheme.colors.secondary.copy(alpha = 0.1f),
                            iconTint = MaterialTheme.colors.secondary
                        )
                    }
                }

                // Company Details
                state.company?.let { company ->
                    CompanyDetailsCard(company = company)
                }

                // Chart Type Selector
                ChartTypeSelector(
                    selectedChartType = selectedChartType,
                    onSelectChartType = { selectedChartType = it }
                )

                // Display the selected chart and information
                when (selectedChartType) {
                    ChartType.INTRADAY -> {
                        IntradaySection(intradayInfos = state.stockInfos)
                    }
                    ChartType.WEEKLY -> {
                        WeeklySection(weekInfos = state.weekInfos)
                    }
                    ChartType.MONTHLY -> {
                        MonthlySection(monthInfos = state.monthInfos)
                    }
                }

                // GPT Analysis
                state.gptmesg?.let { gptMessage ->
                    GPTAnalysisCard(gptMessage = gptMessage)
                }
            }
        }

        // Loading State
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        // Error State
        state.error?.let { error ->
            ErrorMessage(text = error)
        }
    }
}