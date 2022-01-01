package com.depromeet.sloth.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.depromeet.sloth.BuildConfig
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitServiceGenerator {
    private const val timeoutRead = 30
    private const val timeoutConnect = 30

    private fun setClient(authToken: String? = null): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        return httpClient.apply {
            addInterceptor(AuthenticationInterceptor(authToken))
            addInterceptor(HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            })
            connectTimeout(timeoutConnect.toLong(), TimeUnit.SECONDS)
            readTimeout(timeoutRead.toLong(), TimeUnit.SECONDS)
        }.build()
    }

    fun build(accessToken: String? = null): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SLOTH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(setClient(accessToken))
            .build()
    }
}