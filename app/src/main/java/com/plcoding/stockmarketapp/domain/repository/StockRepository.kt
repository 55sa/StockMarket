package com.plcoding.stockmarketapp.domain.repository

import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import com.plcoding.stockmarketapp.domain.model.MonthlyInfo
import com.plcoding.stockmarketapp.domain.model.WeeklyInfo
import com.plcoding.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import java.io.InputStream

interface StockRepository {

    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>

    suspend fun getIntradayInfo(
        symbol: String
    ): Resource<List<IntradayInfo>>

    suspend fun getCompanyInfo(
        symbol: String
    ): Resource<CompanyInfo>


    suspend fun getWatchlistWithDetails(): Resource<List<CompanyListing>>

    suspend fun isDatabaseInitialized(): Boolean

    suspend fun initializeDatabaseFromRemote(): Resource<Unit>

    suspend fun analyzeIntradayInfoWithGpt(tradeInfoList: List<IntradayInfo>): Resource<String>

    suspend fun addToWatchList(symbol: String): Unit

    suspend fun deleteFromWatchList(symbol: String): Unit

    suspend fun isSymbolInWatchlist(symbol: String): Boolean

    suspend fun getUserFileUrl(userId: String): String
    suspend fun fetchStreamFromUrl(url: String): InputStream?
    suspend fun getMonthlyInfo(symbol: String): Resource<List<MonthlyInfo>>
    suspend fun getWeeklyInfo(symbol: String): Resource<List<WeeklyInfo>>
}