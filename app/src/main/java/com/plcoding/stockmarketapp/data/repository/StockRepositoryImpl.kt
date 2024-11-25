package com.plcoding.stockmarketapp.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.opencsv.CSVReader
import com.plcoding.stockmarketapp.data.csv.CSVParser
import com.plcoding.stockmarketapp.data.csv.CompanyListingsParser
import com.plcoding.stockmarketapp.data.local.StockDatabase
import com.plcoding.stockmarketapp.data.local.WatchlistEntity
import com.plcoding.stockmarketapp.data.mapper.toCompanyInfo
import com.plcoding.stockmarketapp.data.mapper.toCompanyListing
import com.plcoding.stockmarketapp.data.mapper.toCompanyListingEntity
import com.plcoding.stockmarketapp.data.remote.GptApi
import com.plcoding.stockmarketapp.data.remote.StockApi
import com.plcoding.stockmarketapp.data.remote.dto.GptRequest
import com.plcoding.stockmarketapp.data.remote.dto.Message
import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.io.InputStreamReader
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val gptApi: GptApi,
    private val db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>,
    private val intradayInfoParser: CSVParser<IntradayInfo>,
): StockRepository {

    private val dao = db.stockdao
    private val watchDao = db.watchdao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if(shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }
            val remoteListings = try {
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())
            } catch(e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListings(
                    listings.map { it.toCompanyListingEntity() }
                )
                emit(Resource.Success(
                    data = dao
                        .searchCompanyListing("")
                        .map { it.toCompanyListing() }
                ))
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return try {
            val response = api.getIntradayInfo(symbol)
            val results = intradayInfoParser.parse(response.byteStream())
            Resource.Success(results)
        } catch(e: IOException) {
            e.printStackTrace()
            Resource.Error(
                message = "Couldn't load intraday info"
            )
        } catch(e: HttpException) {
            e.printStackTrace()
            Resource.Error(
                message = "Couldn't load intraday info"
            )
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val result = api.getCompanyInfo(symbol)
            Resource.Success(result.toCompanyInfo())
        } catch(e: IOException) {
            e.printStackTrace()
            Resource.Error(
                message = "Couldn't load company info"
            )
        } catch(e: HttpException) {
            e.printStackTrace()
            Resource.Error(
                message = "Couldn't load company info"
            )
        }
    }

    override suspend fun getWatchlistWithDetails(): Resource<List<CompanyListing>> {
        return try {
            val watchlistWithDetails = watchDao.getWatchlistWithDetails()
            val mappedResult = watchlistWithDetails.map { it.toCompanyListing() }
            Resource.Success(mappedResult)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error("Failed to load watchlist details")
        }
    }

    override suspend fun isDatabaseInitialized(): Boolean {
        return dao.getCompanyListingCount() > 0
    }

    override suspend fun initializeDatabaseFromRemote(): Resource<Unit> {
        return try {
            val response = api.getListings()
            val parsedListings = companyListingsParser.parse(response.byteStream())

            // Save to database
            dao.clearCompanyListings()
            dao.insertCompanyListings(parsedListings.map { it.toCompanyListingEntity() })

            Resource.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error("Failed to initialize database: ${e.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun analyzeIntradayInfoWithGpt(tradeInfoList: List<IntradayInfo>): Resource<String> {
        return try {
            // Build the GPT request
            val tradesSummary = tradeInfoList.joinToString("\n") {
                "Timestamp: ${it.date}, Close: ${it.close}"
            }
            val prompt = """
            You are a financial analyst. Analyze the following intraday trading data and provide insights, such as patterns, anomalies, or trends in few sentences:
            
            $tradesSummary
        """.trimIndent()

            val gptRequest = GptRequest(
                model = "gpt-3.5-turbo", // Ensure tshe model is included
                messages = listOf(Message(role = "user", content = prompt)),
                max_tokens = 200,
                temperature = 0.7
            )

            // Send request to GPT API
            val response = gptApi.analyzeIntradayInfo(gptRequest)

            val jsonResponse = response.string()
            val content = extractContentFromJson(jsonResponse)


            Resource.Success(content) // Convert `ResponseBody` to string
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error("Failed to analyze with GPT: ${e.message}")
        }
    }

    override suspend fun addToWatchList(symbol: String) {
        watchDao.insertwatch(
            WatchlistEntity(symbol = symbol)
        )
    }


    private fun extractContentFromJson(jsonResponse: String): String {
        return try {
            val root = JSONObject(jsonResponse)
            val choices = root.getJSONArray("choices")
            if (choices.length() > 0) {
                val firstChoice = choices.getJSONObject(0)
                val message = firstChoice.getJSONObject("message")
                message.getString("content")
            } else {
                "No content found"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Failed to parse content"
        }
    }


}