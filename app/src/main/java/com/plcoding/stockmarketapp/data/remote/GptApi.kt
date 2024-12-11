package com.plcoding.stockmarketapp.data.remote

import com.plcoding.stockmarketapp.data.remote.dto.GptRequest
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface GptApi {

    @POST("chat/completions")
    suspend fun analyzeIntradayInfo(
        @Body request: GptRequest
    ): ResponseBody


    companion object {
        const val BASE_URL = "https://api.openai.com/v1/"
    }
}
