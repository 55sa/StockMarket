package com.plcoding.stockmarketapp.presentation.company_info

import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import com.plcoding.stockmarketapp.domain.model.MonthlyInfo
import com.plcoding.stockmarketapp.domain.model.WeeklyInfo

data class CompanyInfoState(
    val stockInfos: List<IntradayInfo> = emptyList(),
    val weekInfos: List<WeeklyInfo> = emptyList(),
    val monthInfos: List<MonthlyInfo> = emptyList(),
    val company: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val isInWatchList: Boolean = false,
    val error: String? = null,
    val gptmesg: String? =null
)
