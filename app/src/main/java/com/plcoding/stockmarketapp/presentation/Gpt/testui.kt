package com.plcoding.stockmarketapp.presentation.Gpt

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.plcoding.stockmarketapp.util.Resource
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination
fun GptAnalysisScreen(viewModel: GptViewModel = hiltViewModel()) {
    val gptResponse by viewModel.gptResponse.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (gptResponse) {
            is Resource.Loading -> {
                CircularProgressIndicator()
            }
            is Resource.Success -> {
                Text(
                    text = "GPT Analysis:\n${(gptResponse as Resource.Success).data}",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is Resource.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error occurred:",
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (gptResponse as Resource.Error).message ?: "Unknown error",
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    }
}
