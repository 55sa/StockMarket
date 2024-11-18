package com.plcoding.stockmarketapp.presentation.mainscreen

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.stockmarketapp.R
import com.ramcosta.composedestinations.annotation.Destination

@Composable
fun HomePageScreen() {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "StockEa\uD83D\uDE9E",
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
            text = "IHSG",
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
            Text("Chart Placeholder", color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Updated Stock Data Summary Section
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
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Lot", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("186.26 M", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Value", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("9.88 T", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Freq", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("1.10 M", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Watchlist",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        LazyColumn {
            items(
                listOf(
                    Pair("Tesla", R.drawable.tesla_logo),
                    Pair("Amazon", R.drawable.amazon_logo),
                    Pair("Apple", R.drawable.apple_logo)
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
}

@Composable
fun WatchlistItem(name: String, price: String, change: String, color: Color, logo: Painter) {

}


@Preview(showBackground = true)
@Composable
fun HomePageScreenPreview() {
    HomePageScreen()
}

@Destination
@Composable
fun HomeScreen() {
    HomePageScreen()
}


