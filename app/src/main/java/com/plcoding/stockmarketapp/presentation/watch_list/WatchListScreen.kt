package com.plcoding.stockmarketapp.presentation.watch_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plcoding.stockmarketapp.presentation.company_listings.CompanyItem
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination(start = true)
fun WatchListScreen(
    viewModel: WatchListViewModel = hiltViewModel()
) {
    val state = viewModel.state
    Column(modifier = Modifier.fillMaxSize()) {
        // 显示标题
        Text(
            text = "我的关注列表",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(16.dp)
        )

        // 显示加载动画

            // 显示关注列表内容
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.companies) { company ->
                    CompanyItem(
                        company = company,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }

