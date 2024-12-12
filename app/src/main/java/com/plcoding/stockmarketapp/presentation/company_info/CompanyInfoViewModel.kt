package com.plcoding.stockmarketapp.presentation.company_info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: StockRepository
) : ViewModel() {

    var state by mutableStateOf(CompanyInfoState())

    init {
        viewModelScope.launch {
            val symbol = savedStateHandle.get<String>("symbol") ?: return@launch
            state = state.copy(isLoading = true)

            val companyInfoDeferred = async { repository.getCompanyInfo(symbol) }
            val intradayInfoDeferred = async { repository.getIntradayInfo(symbol) }
            val monthlyInfoDeferred = async { repository.getMonthlyInfo(symbol) }
            val weeklyInfoDeferred = async { repository.getWeeklyInfo(symbol) }

            // Fetch company info
            when (val result = companyInfoDeferred.await()) {
                is Resource.Success -> {
                    state = state.copy(company = result.data, error = null)
                }
                is Resource.Error -> {
                    state = state.copy(error = result.message, isLoading = false)
                }
                else -> Unit
            }

            // Fetch intraday info and analyze with GPT
            when (val result = intradayInfoDeferred.await()) {
                is Resource.Success -> {
                    state = state.copy(stockInfos = result.data ?: emptyList(), error = null)

                    val gptAnalysisDeferred = async { repository.analyzeIntradayInfoWithGpt(state.stockInfos) }
                    when (val gptResult = gptAnalysisDeferred.await()) {
                        is Resource.Success -> {
                            state = state.copy(gptmesg = gptResult.data, isLoading = false)
                        }
                        is Resource.Error -> {
                            state = state.copy(error = gptResult.message, isLoading = false)
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = gptResult.isLoading)
                        }
                    }
                }
                is Resource.Error -> {
                    state = state.copy(error = result.message, isLoading = false)
                }
                else -> Unit
            }
        }
    }

    fun checkIfInWatchlist(symbol: String) {
        viewModelScope.launch {
            val isInWatchlist = repository.isSymbolInWatchlist(symbol)
            state = state.copy(isInWatchList = isInWatchlist)
        }
    }

    fun addToWatchList(symbol: String) {
        viewModelScope.launch {
            repository.addToWatchList(symbol)
            state = state.copy(isInWatchList = true)
        }
    }
}
