package com.plcoding.stockmarketapp.presentation.Main_Screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    private val _nasdaqData = MutableStateFlow<Resource<List<IntradayInfo>>>(Resource.Loading())
    val nasdaqData: StateFlow<Resource<List<IntradayInfo>>> = _nasdaqData

    private val _watchlist = MutableStateFlow<Resource<List<CompanyListing>>>(Resource.Loading())
    val watchlist: StateFlow<Resource<List<CompanyListing>>> = _watchlist

    init {
        viewModelScope.launch {
            initializeData()
        }
        loadNasdaqData()
        loadWatchlist()
    }

    fun loadNasdaqData() {
        viewModelScope.launch {
            _nasdaqData.value = repository.getIntradayInfo("TSLA")
        }
    }

    fun loadWatchlist() {
        viewModelScope.launch {
            _watchlist.value = repository.getWatchlistWithDetails()
        }
    }

    fun removeFromWatchlist(symbol: String) {
        viewModelScope.launch {
            repository.deleteFromWatchList(symbol)
            loadWatchlist()
        }
    }

    private suspend fun initializeData() = withContext(Dispatchers.IO) {
        val isDatabaseInitialized = repository.isDatabaseInitialized()
        if (!isDatabaseInitialized) {
            initializeDatabase()
        }
        loadWatchlist()
    }


    private suspend fun initializeDatabase() {
        repository.initializeDatabaseFromRemote()
    }
}