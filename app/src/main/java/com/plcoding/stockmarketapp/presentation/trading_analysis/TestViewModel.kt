package com.plcoding.stockmarketapp.presentation.trading_analysis

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.presentation.Login.AuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    // Exposing file URL as a state flow
    private val _userFileUrl = MutableStateFlow<String?>(null)
    val userFileUrl: StateFlow<String?> = _userFileUrl.asStateFlow()

    // Fetch the user file URL using the repository
    fun fetchUserFileUrl(userId: String) {
        viewModelScope.launch {
            try {
                val url = repository.getUserFileUrl(userId)
                _userFileUrl.value = url
            } catch (e: Exception) {
                _userFileUrl.value = null // Handle errors gracefully
            }
        }
    }
}