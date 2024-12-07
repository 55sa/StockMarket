//package com.plcoding.stockmarketapp.presentation.Gpt
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.plcoding.stockmarketapp.data.remote.dto.GptRequest
//import com.plcoding.stockmarketapp.data.remote.dto.Message
//import com.plcoding.stockmarketapp.domain.model.IntradayInfo
//import com.plcoding.stockmarketapp.domain.repository.StockRepository
//import com.plcoding.stockmarketapp.util.Resource
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
//import javax.inject.Inject
//
//@HiltViewModel
//class GptViewModel @Inject constructor(
//    private val repository: StockRepository // Inject your repository
//) : ViewModel() {
//
//    private val _gptResponse = MutableStateFlow<Resource<String>>(Resource.Loading())
//    val gptResponse: StateFlow<Resource<String>> = _gptResponse
//
//    init {
//        analyzeHardcodedData()
//    }
//
//    private fun analyzeHardcodedData() {
//        // Hardcoded IntradayInfo data for testing with consistent ISO 8601 format
//        val hardcodedData = listOf(
//            IntradayInfo(date = formatDate("2024-11-15 19:00:00"), close = 150.25),
//            IntradayInfo(date = formatDate("2024-11-12 11:00:00"), close = 151.35),
//            IntradayInfo(date = formatDate("2024-11-12 12:00:00"), close = 149.85),
//            IntradayInfo(date = formatDate("2024-11-12 13:00:00"), close = 150.10),
//            IntradayInfo(date = formatDate("2024-11-12 14:00:00"), close = 152.50)
//        )
//
//        // Call the GPT API using the repository
//        viewModelScope.launch {
//            val result = repository.analyzeIntradayInfoWithGpt(hardcodedData)
//            _gptResponse.value = result
//        }
//    }
//
//    fun formatDate(date: String): LocalDateTime {
//        // 定义输入日期格式
//        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//
//        return try {
//            // 解析字符串为 LocalDateTime
//            LocalDateTime.parse(date, inputFormatter)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            // 如果解析失败，抛出异常或者返回当前时间作为默认值
//            LocalDateTime.now()
//        }
//    }
//
//
//}