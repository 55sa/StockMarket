package com.plcoding.stockmarketapp.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.database.FirebaseDatabase
import com.opencsv.CSVReader
import com.plcoding.stockmarketapp.data.csv.CSVParser
import com.plcoding.stockmarketapp.data.csv.CompanyListingsParser
import com.plcoding.stockmarketapp.data.csv.MonthlyInfoParser
import com.plcoding.stockmarketapp.data.csv.WeeklyInfoParser
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
import com.plcoding.stockmarketapp.domain.model.MonthlyInfo
import com.plcoding.stockmarketapp.domain.model.WeeklyInfo
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Implementation of [StockRepository] responsible for managing stock data operations,
 * including database access, remote API calls, and analysis.
 */
@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val gptApi: GptApi,
    private val db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>,
    private val intradayInfoParser: CSVParser<IntradayInfo>,
    private val monthlyInfoParser: CSVParser<MonthlyInfo>,   // 新增
    private val weeklyInfoParser: CSVParser<WeeklyInfo>
): StockRepository {

    private val database = FirebaseDatabase.getInstance().reference
    private val dao = db.stockdao
    private val watchDao = db.watchdao

    /**
     * Retrieves company listings from local cache or remote API based on fetch conditions.
     */
    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> = flow {
        emit(Resource.Loading(true))

        // Move database operation to Dispatchers.IO
        val localListings = withContext(Dispatchers.IO) {
            dao.searchCompanyListing(query)
        }

        emit(Resource.Success(
            data = localListings.map { it.toCompanyListing() }
        ))

        val isDbEmpty = localListings.isEmpty() && query.isBlank()
        val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
        if (shouldJustLoadFromCache) {
            emit(Resource.Loading(false))
            return@flow
        }

        val remoteListings = try {
            val response = api.getListings()
            companyListingsParser.parse(response.byteStream())
        } catch (e: IOException) {
            e.printStackTrace()
            emit(Resource.Error("Couldn't load data"))
            null
        } catch (e: HttpException) {
            e.printStackTrace()
            emit(Resource.Error("Couldn't load data"))
            null
        }

        remoteListings?.let { listings ->
            withContext(Dispatchers.IO) {
                dao.clearCompanyListings()
                dao.insertCompanyListings(
                    listings.map { it.toCompanyListingEntity() }
                )
            }
            emit(Resource.Success(
                data = dao
                    .searchCompanyListing("")
                    .map { it.toCompanyListing() }
            ))
            emit(Resource.Loading(false))
        }
    }

    /**
     * Fetches intraday stock information for the specified symbol.
     */
    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> = withContext(Dispatchers.IO) {
         try {
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

    /**
     * Fetches monthly stock information for the specified symbol.
     */
    override suspend fun getMonthlyInfo(symbol: String): Resource<List<MonthlyInfo>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getMonthlyInfo(symbol)
            val results = monthlyInfoParser.parse(response.byteStream())
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

    /**
     * Fetches weekly stock information for the specified symbol.
     */
    override suspend fun getWeeklyInfo(symbol: String): Resource<List<WeeklyInfo>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getWeeklyInfo(symbol)
            val results = weeklyInfoParser.parse(response.byteStream())
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

    /**
     * Retrieves company information for the specified symbol.
     */
    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> = withContext(Dispatchers.IO) {
         try {
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

    /**
     * Retrieves watchlist details and maps them to [CompanyListing].
     */
    override suspend fun getWatchlistWithDetails(): Resource<List<CompanyListing>> = withContext(Dispatchers.IO) {
        try {
            val watchlistWithDetails = watchDao.getWatchlistWithDetails()
            val mappedResult = watchlistWithDetails.map { it.toCompanyListing() }
            Resource.Success(mappedResult)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error("Failed to load watchlist details")
        }
    }


    /**
     * Checks if the database has been initialized with company listings.
     */
    override suspend fun isDatabaseInitialized(): Boolean = withContext(Dispatchers.IO) {
        dao.getCompanyListingCount() > 0
    }


    /**
     * Initializes the database with company listings fetched from the remote API.
     */
    override suspend fun initializeDatabaseFromRemote(): Resource<Unit> = withContext(Dispatchers.IO) {
         try {
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

    /**
     * Analyzes intraday trading data using GPT API for insights.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun analyzeIntradayInfoWithGpt(tradeInfoList: List<IntradayInfo>): Resource<String> = withContext(Dispatchers.IO) {
         try {
            val tradesSummary = tradeInfoList.joinToString("\n") {
                "Timestamp: ${it.date}, Close: ${it.close}"
            }
            val prompt = """
            You are a financial analyst. Analyze the following intraday trading data and provide insights, such as patterns, anomalies, or trends in few sentences:
            
            $tradesSummary
        """.trimIndent()

            val gptRequest = GptRequest(
                model = "gpt-3.5-turbo",
                messages = listOf(Message(role = "user", content = prompt)),
                max_tokens = 200,
                temperature = 0.7
            )

            val response = gptApi.analyzeIntradayInfo(gptRequest)

            val jsonResponse = response.string()
            val content = extractContentFromJson(jsonResponse)


            Resource.Success(content)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error("Failed to analyze with GPT: ${e.message}")
        }
    }


    override suspend fun addToWatchList(symbol: String) = withContext(Dispatchers.IO) {
        watchDao.insertwatch(
            WatchlistEntity(symbol = symbol)
        )
    }

    override suspend fun deleteFromWatchList(symbol: String): Unit = withContext(Dispatchers.IO) {
        watchDao.deleteBySymbol(symbol)
    }

    override suspend fun isSymbolInWatchlist(symbol: String): Boolean = withContext(Dispatchers.IO) {
         watchDao.isSymbolInWatchlist(symbol)
    }

    override suspend fun getUserFileUrl(userId: String): String = withContext(Dispatchers.IO) {
       try {
            val snapshot = database.child(userId).get().await()
            snapshot.getValue(String::class.java).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            "*"
        }
    }

    /**
     * Extracts content from GPT API JSON response.
     */
    private suspend fun extractContentFromJson(jsonResponse: String): String = withContext(Dispatchers.IO) {
       try {
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

    /**
     * Fetches a data stream from the specified URL.
     */
  override suspend fun fetchStreamFromUrl(url: String): InputStream? = withContext(Dispatchers.IO) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            println("Failed to fetch stream: ${response.code}")
            return@withContext null
        }

        response.body?.byteStream()
    }


}