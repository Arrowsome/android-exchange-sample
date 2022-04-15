package com.example.exchange.data

import com.example.exchange.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeService {
    /**
     * Latest exchange rate inquiry
     */
    @GET("latest")
    suspend fun getRate(
        @Query("base") base: String = "EUR",
        @Query("symbols") symbols: String = "USD,GBP",
        @Query("access_key") accessKey: String = BuildConfig.API_ACCESS_KEY,
    ): Response<ExchangeRate>

    companion object {
        private const val BASE_URL = "http://api.exchangeratesapi.io/v1/"

        fun create(): ExchangeService {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                // TODO: switch to Moshi
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ExchangeService::class.java)
        }
    }
}