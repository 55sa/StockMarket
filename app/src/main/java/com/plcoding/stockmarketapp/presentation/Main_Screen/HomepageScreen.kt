package com.plcoding.stockmarketapp.presentation.Main_Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectAsState

@Composable
fun HomepageScreen(viewModel: StockViewModel) {
    val stockData = viewModel.stockData.collectAsState()
    val watchlistData = viewModel.watchlistData.collectAsState()

}



