package com.plcoding.stockmarketapp.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.plcoding.stockmarketapp.BuildConfig
import com.plcoding.stockmarketapp.data.csv.CSVParser
import com.plcoding.stockmarketapp.data.local.StockDatabase
import com.plcoding.stockmarketapp.data.local.StockDatabase.Companion.MIGRATION_1_2
import com.plcoding.stockmarketapp.data.remote.GptApi
import com.plcoding.stockmarketapp.data.remote.StockApi
import com.plcoding.stockmarketapp.data.repository.StockRepositoryImpl
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import com.plcoding.stockmarketapp.domain.model.MonthlyInfo
import com.plcoding.stockmarketapp.domain.model.WeeklyInfo
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.presentation.Login.GoogleAuthUiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides a singleton instance of [StockApi] configured with Retrofit.
     * Includes a logging interceptor for basic HTTP logs.
     *
     * @return A configured instance of [StockApi].
     */
    @Provides
    @Singleton
    fun provideStockApi(): StockApi {
        return Retrofit.Builder()
            .baseUrl(StockApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }).build())
            .build()
            .create()
    }

    /**
     * Provides a singleton instance of [GptApi] configured with Retrofit.
     * Adds an authorization header and JSON content type for OpenAI requests.
     * Includes a logging interceptor for detailed HTTP body logs.
     *
     * @return A configured instance of [GptApi].
     */
    @Provides
    @Singleton
    fun provideGptApi(): GptApi {
        val apiKey = BuildConfig.OPENAI_API_KEY
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer "+ apiKey)
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()

        return Retrofit.Builder()
            .baseUrl(GptApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
            .create(GptApi::class.java)
    }


    /**
     * Provides a singleton instance of the [StockDatabase] using Room.
     * Configured with database migration strategy from version 1 to 2.
     *
     * @param app The application context.
     * @return A configured instance of [StockDatabase].
     */
    @Provides
    @Singleton
    fun provideStockDatabase(app: Application): StockDatabase {
        return Room.databaseBuilder(
            app,
            StockDatabase::class.java,
            "stockdb.db"
        ).addMigrations(MIGRATION_1_2).build()
    }

    /**
     * Provides a singleton instance of [StockRepository] for accessing stock data.
     * Injects required dependencies such as APIs, database, and parsers.
     *
     * @param api The [StockApi] instance for network requests.
     * @param gptApi The [GptApi] instance for GPT-related requests.
     * @param db The local [StockDatabase] instance.
     * @param companyListingsParser Parser for company listings data.
     * @param intradayInfoParser Parser for intraday stock data.
     * @param monthlyInfoParser Parser for monthly stock data.
     * @param weeklyInfoParser Parser for weekly stock data.
     * @return A configured instance of [StockRepository].
     */
    @Module
    @InstallIn(SingletonComponent::class)
    object AppModule {
        @Provides
        @Singleton
        fun provideStockRepository(
            api: StockApi,
            gptApi: GptApi,
            db: StockDatabase,
            companyListingsParser: CSVParser<CompanyListing>,
            intradayInfoParser: CSVParser<IntradayInfo>,
            monthlyInfoParser: CSVParser<MonthlyInfo>,
            weeklyInfoParser: CSVParser<WeeklyInfo>

        ): StockRepository {
            return StockRepositoryImpl(api, gptApi, db, companyListingsParser, intradayInfoParser,
                monthlyInfoParser, weeklyInfoParser)
        }
    }

    /**
     * Provides a singleton instance of [SignInClient] for Google authentication.
     *
     * @param context The application context.
     * @return A configured instance of [SignInClient].
     */
    @Provides
    @Singleton
    fun provideSignInClient(
        @ApplicationContext context: Context
    ): SignInClient {
        return Identity.getSignInClient(context)
    }

    /**
     * Provides a singleton instance of [GoogleAuthUiClient] for handling Google sign-in UI.
     *
     * @param context The application context.
     * @param signInClient The [SignInClient] instance for managing sign-in requests.
     * @return A configured instance of [GoogleAuthUiClient].
     */
    @Provides
    @Singleton
    fun provideGoogleAuthUiClient(
        @ApplicationContext context: Context,
        signInClient: SignInClient
    ): GoogleAuthUiClient {
        return GoogleAuthUiClient(context, signInClient)
    }





}