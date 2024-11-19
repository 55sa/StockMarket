package com.plcoding.stockmarketapp.presentation.mainscreen

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.stockmarketapp.R
import com.ramcosta.composedestinations.annotation.Destination
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun HomePageScreen() {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Text(
                text = "StockEasy",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            BasicTextField(
                value = searchQuery,
                onValueChange = { newQuery -> searchQuery = newQuery },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Gray.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        if (searchQuery.isEmpty()) Text("Search", color = Color.Gray)
                        innerTextField()
                    }
                }
            )

            Text(
                text = "US $",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = "7,056.04",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "-35.72 (-0.50%)",
                style = MaterialTheme.typography.body1,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                StockLineChart()
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Open", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("7,091.76", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("High", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("7,100.81", color = Color.Green, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Low", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("7,016.70", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.weight(1f)
                ) {

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Close", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("7,340.60", color = Color.Green, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Volume", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("1.10 M", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Watchlist",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 5.dp)
            )
            LazyColumn {
                items(
                    listOf(
                        Pair("TSLA", R.drawable.tesla_logo),
                        Pair("AMZN", R.drawable.amazon_logo),
                        Pair("AAPL", R.drawable.apple_logo)
                    )
                ) { pair ->
                    val (name, logoRes) = pair
                    WatchlistItem(
                        name = name,
                        price = "198",
                        change = "+1 (+0.53%)",
                        color = if (name == "Amazon") Color.Red else Color.Green,
                        logo = painterResource(id = logoRes)
                    )
                }
            }
        }

        BottomNavigationBar()
    }
}

@Composable
fun WatchlistItem(name: String, price: String, change: String, color: Color, logo: Painter) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = logo,
                contentDescription = "$name logo",
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 8.dp)
            )
            Column {
                Text(text = name, fontWeight = FontWeight.Bold)
                Text(
                    text = when (name) {
                        "TSLA" -> "Tesla Inc."
                        "AMZN" -> "Amazon.com, Inc."
                        "AAPL" -> "Apple Inc."
                        else -> "Company"
                    },
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(price, fontWeight = FontWeight.Bold)
            Text(
                change,
                color = color,
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
fun BottomNavigationBar() {
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Black

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

@Composable
fun StockLineChart() {
    val stockPrices = listOf(350f, 460f, 984f, 3451f, 327f, 689f, 214f, 200f, 500f, 7050f,
                             7100f, 7020f, 660f, 7005f, 7105f, 2120f, 7080f, )

    val maxPrice = stockPrices.maxOrNull() ?: 0f
    val minPrice = stockPrices.minOrNull() ?: 0f

    Canvas(modifier = Modifier.fillMaxSize()) {
        val chartWidth = size.width
        val chartHeight = size.height
        val spacing = chartWidth / (stockPrices.size - 1)

        val path = Path().apply {
            for (i in stockPrices.indices) {
                val x = i * spacing
                val y = chartHeight - (stockPrices[i] - minPrice) / (maxPrice - minPrice) * chartHeight
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
        }

        val fillPath = Path().apply {
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color.Red.copy(alpha = 0.5f), Color.Transparent),
                endY = size.height
            )
        )

        drawPath(
            path = path,
            color = Color.Red,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Destination
@Composable
fun HomeScreen() {
    HomePageScreen()
}

@Preview(showBackground = true)
@Composable
fun HomePageScreenPreview() {
    HomePageScreen()
}
