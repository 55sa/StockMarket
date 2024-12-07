package com.plcoding.stockmarketapp.presentation.watch_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.presentation.company_listings.CompanyListingsState
import com.plcoding.stockmarketapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchListViewModel @Inject constructor(
    private val repository: StockRepository,
) : ViewModel() {

    var state by mutableStateOf(CompanyListingsState())

    init {
        viewModelScope.launch {
            initializeData()
        }
    }

    private suspend fun initializeData() {

        val isDatabaseInitialized = repository.isDatabaseInitialized()
        if (!isDatabaseInitialized) {
            initializeDatabase()
        }


        loadWatchlist()
    }

    private suspend fun initializeDatabase() {
        state = state.copy(isLoading = true)
        val result = repository.initializeDatabaseFromRemote()
        when (result) {
            is Resource.Success -> {

                state = state.copy(isLoading = false)
            }
            is Resource.Error -> {

                state = state.copy(
                    isLoading = false

                )
            }
        }
    }

    private fun loadWatchlist() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            when (val result = repository.getWatchlistWithDetails()) {
                is Resource.Success -> {
                    result.data?.let { companies ->
                        state = state.copy(companies = companies, isLoading = false)
                    }
                }
                is Resource.Error -> {
                    state = state.copy(
                        isLoading = false

                    )
                }
                is Resource.Loading -> {
                    state = state.copy(isLoading = result.isLoading)
                }
            }
        }
    }


    private fun getCompanyListings(
        query: String = state.searchQuery.lowercase(),
        fetchFromRemote: Boolean = false
    ) {
        viewModelScope.launch {
            repository.getCompanyListings(fetchFromRemote, query)
        }
    }
}



