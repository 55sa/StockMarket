package com.plcoding.stockmarketapp.presentation.company_info

import android.content.res.Configuration
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
import androidx.compose.ui.platform.LocalConfiguration
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
import com.plcoding.stockmarketapp.ui.theme.DarkThemeColors
import com.plcoding.stockmarketapp.ui.theme.LightThemeColors
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

    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

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
                color = colorTheme.primaryText
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.body2.copy(
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Italic,
                color = colorTheme.primaryText
            )
        )
    }
}

@Composable
fun IntradayInfoCard(intradayInfo: IntradayInfo) {

    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = colorTheme.secondaryContainer,
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
                    color = colorTheme.primaryText
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

    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = colorTheme.secondaryContainer,
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
                    color = colorTheme.primaryText
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
    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = colorTheme.secondaryContainer,
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
                    color = colorTheme.primaryText
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
    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

    Surface(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .height(40.dp)
            .clickable(onClick = onClick),
        color = if (isSelected) colorTheme.primaryContainer else colorTheme.surface,
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
        ChartTypeButton(
            text = "Intraday",
            isSelected = selectedChartType == ChartType.INTRADAY,
            onClick = { onSelectChartType(ChartType.INTRADAY) }
        )
        ChartTypeButton(
            text = "Weekly",
            isSelected = selectedChartType == ChartType.WEEKLY,
            onClick = { onSelectChartType(ChartType.WEEKLY) }
        )
        ChartTypeButton(
            text = "Monthly",
            isSelected = selectedChartType == ChartType.MONTHLY,
            onClick = { onSelectChartType(ChartType.MONTHLY) }
        )
    }
}

@Composable
fun CompanyDetailsCard(company: CompanyInfo) {
    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = colorTheme.secondaryContainer,
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
                    color = colorTheme.primaryText
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = company.symbol,
                style = MaterialTheme.typography.subtitle1.copy(
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Monospace,
                    color = colorTheme.primaryText
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
                    color = colorTheme.primaryText
                )
            )
            Text(
                text = "Country: ${company.country}",
                style = MaterialTheme.typography.body1.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic,
                    color = colorTheme.primaryText
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
                    color = colorTheme.primaryText
                ),
                maxLines = 10,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun GPTAnalysisCard(gptMessage: String) {
    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "AI Buffett Analysis:",
        style = MaterialTheme.typography.h6.copy(
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = colorTheme.primaryText
        )
    )
    Spacer(modifier = Modifier.height(8.dp))
    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = colorTheme.secondaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = gptMessage,
            style = MaterialTheme.typography.body2.copy(
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                color = colorTheme.primaryText
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
        )
    )
}

// 以下是分别用于横屏模式拆分的Chart与Data展示Section
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IntradayChartSection(intradayInfos: List<IntradayInfo>) {
    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

    if (intradayInfos.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Market Summary",
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = colorTheme.primaryText
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
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IntradayDataSection(intradayInfos: List<IntradayInfo>) {
    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

    if (intradayInfos.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Stock Details",
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = colorTheme.primaryText
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        IntradayInfoCard(intradayInfo = intradayInfos.last())
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklyChartSection(weekInfos: List<WeeklyInfo>) {
    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

    if (weekInfos.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Weekly Summary",
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = colorTheme.primaryText
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
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklyDataSection(weekInfos: List<WeeklyInfo>) {
    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

    if (weekInfos.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Weekly Stock Details",
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = colorTheme.primaryText
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        WeeklyInfoCard(weeklyInfo = weekInfos.last())
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthlyChartSection(monthInfos: List<MonthlyInfo>) {
    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

    if (monthInfos.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Monthly Summary",
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = colorTheme.primaryText
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
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthlyDataSection(monthInfos: List<MonthlyInfo>) {
    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors
    if (monthInfos.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Monthly Stock Details",
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = colorTheme.primaryText
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        MonthlyInfoCard(monthlyInfo = monthInfos.last())
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// 竖屏下原有的Section，保留整合的显示（图表+数据）
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IntradaySection(intradayInfos: List<IntradayInfo>) {

    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

    if (intradayInfos.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Intraday Summary",
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = colorTheme.primaryText
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
                color = colorTheme.primaryText
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        IntradayInfoCard(intradayInfo = intradayInfos.last())
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklySection(weekInfos: List<WeeklyInfo>) {

    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

    if (weekInfos.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Weekly Summary",
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = colorTheme.primaryText
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
                color = colorTheme.primaryText
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        WeeklyInfoCard(weeklyInfo = weekInfos.last())
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthlySection(monthInfos: List<MonthlyInfo>) {
    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

    if (monthInfos.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Monthly Summary",
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = colorTheme.primaryText
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
                color = colorTheme.primaryText
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
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

    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

    LaunchedEffect(Unit) {
        viewModel.checkIfInWatchlist(symbol)
    }

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorTheme.screenBackgroundColor)
    ) {
        if (state.error == null) {
            if (isPortrait) {
                // 竖屏：单列显示（原有逻辑）
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TopActionRow(state = state, symbol = symbol, navigator = navigator, viewModel = viewModel)

                    state.company?.let { company ->
                        CompanyDetailsCard(company = company)
                    }

                    ChartTypeSelector(
                        selectedChartType = selectedChartType,
                        onSelectChartType = { selectedChartType = it }
                    )

                    when (selectedChartType) {
                        ChartType.INTRADAY -> IntradaySection(intradayInfos = state.stockInfos)
                        ChartType.WEEKLY -> WeeklySection(weekInfos = state.weekInfos)
                        ChartType.MONTHLY -> MonthlySection(monthInfos = state.monthInfos)
                    }

                    state.gptmesg?.let { gptMessage ->
                        GPTAnalysisCard(gptMessage = gptMessage)
                    }
                }
            } else {
                // 横屏模式：分左右两列
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // 左列：顶部操作行 + 公司信息 + GPT分析 + 数据部分
                    Column(
                        modifier = Modifier
                            .weight(0.4f)
                            .padding(end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TopActionRow(state = state, symbol = symbol, navigator = navigator, viewModel = viewModel)

                        state.company?.let { company ->
                            CompanyDetailsCard(company = company)
                        }

                        state.gptmesg?.let { gptMessage ->
                            GPTAnalysisCard(gptMessage = gptMessage)
                        }


                    }

                    // 右列：图表类型选择器 + 图表
                    Column(
                        modifier = Modifier
                            .weight(0.6f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ChartTypeSelector(
                            selectedChartType = selectedChartType,
                            onSelectChartType = { selectedChartType = it }
                        )

                        when (selectedChartType) {
                            ChartType.INTRADAY -> IntradayChartSection(intradayInfos = state.stockInfos)
                            ChartType.WEEKLY -> WeeklyChartSection(weekInfos = state.weekInfos)
                            ChartType.MONTHLY -> MonthlyChartSection(monthInfos = state.monthInfos)
                        }

                        when (selectedChartType) {
                            ChartType.INTRADAY -> IntradayDataSection(intradayInfos = state.stockInfos)
                            ChartType.WEEKLY -> WeeklyDataSection(weekInfos = state.weekInfos)
                            ChartType.MONTHLY -> MonthlyDataSection(monthInfos = state.monthInfos)
                        }
                    }
                }
            }
        }

        // 加载状态
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        // 错误状态
        state.error?.let { error ->
            ErrorMessage(text = error)
        }
    }
}

@Composable
fun TopActionRow(
    state: CompanyInfoState,
    symbol: String,
    navigator: DestinationsNavigator,
    viewModel: CompanyInfoViewModel
) {
    val colorTheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors

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
            backgroundColor = colorTheme.primaryItem.copy(alpha = 0.1f),
            iconTint = colorTheme.primaryItem
        )

        // 添加到观察列表按钮或已在观察列表的确认图标
        if (!state.isInWatchList) {
            StyledIconButton(
                icon = Icons.Default.Add,
                contentDescription = "Add to Watchlist",
                onClick = { viewModel.addToWatchList(symbol) },
                backgroundColor = colorTheme.primaryItem.copy(alpha = 0.1f),
                iconTint = colorTheme.primaryItem
            )
        } else {
            StyledIconButton(
                icon = Icons.Default.Check,
                contentDescription = "In Watchlist",
                onClick = { /* 点击反馈逻辑 */ },
                backgroundColor = colorTheme.secondaryItem.copy(alpha = 0.1f),
                iconTint = colorTheme.secondaryItem
            )
        }
    }
}