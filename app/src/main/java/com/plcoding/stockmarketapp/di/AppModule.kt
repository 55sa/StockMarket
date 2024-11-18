package com.plcoding.stockmarketapp.di

import android.app.Application
import androidx.room.Room
import com.plcoding.stockmarketapp.data.local.StockDatabase
import com.plcoding.stockmarketapp.data.local.StockDatabase.Companion.MIGRATION_1_2
import com.plcoding.stockmarketapp.data.remote.GptApi
import com.plcoding.stockmarketapp.data.remote.StockApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

    @Provides
    @Singleton
    fun provideGptApi(): GptApi {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer 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")
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



    @Provides
    @Singleton
    fun provideStockDatabase(app: Application): StockDatabase {
        return Room.databaseBuilder(
            app,
            StockDatabase::class.java,
            "stockdb.db"
        ).addMigrations(MIGRATION_1_2).build()
    }
}