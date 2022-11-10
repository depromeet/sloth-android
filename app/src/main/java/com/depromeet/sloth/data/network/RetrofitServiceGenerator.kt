package com.depromeet.sloth.data.network

import com.depromeet.sloth.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RetrofitServiceGenerator @Inject constructor(
    private val accessTokenAuthenticator: AccessTokenAuthenticator,
) {
    private val timeoutRead = 30L
    private val timeoutConnect = 30L

    private fun provideHttpClient(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        return httpClient.apply {
            authenticator(accessTokenAuthenticator)
            addInterceptor(AuthenticationInterceptor())
            addInterceptor(HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            })
            connectTimeout(timeoutConnect, TimeUnit.SECONDS)
            readTimeout(timeoutRead, TimeUnit.SECONDS)
        }.build()
    }

    fun build(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SLOTH_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideHttpClient())
            .build()
    }
}



