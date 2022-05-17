package com.depromeet.sloth.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.depromeet.sloth.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Inject

class RetrofitServiceGenerator @Inject constructor(
    private val accessTokenAuthenticator: AccessTokenAuthenticator,
) {
    private val timeoutRead = 30L
    private val timeoutConnect = 30L

    private fun provideHttpClient(
        authToken: String? = null
    ): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        return httpClient.apply {
            authenticator(accessTokenAuthenticator)
            addInterceptor(AuthenticationInterceptor(authToken))
            addInterceptor(HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            })
            connectTimeout(timeoutConnect, TimeUnit.SECONDS)
            readTimeout(timeoutRead, TimeUnit.SECONDS)
        }.build()
    }

    fun build(
        accessToken: String? = null,
        isGoogleLogin: Boolean = false
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(
                when (isGoogleLogin) {
                    true -> BuildConfig.GOOGLE_BASE_URL
                    false -> BuildConfig.SLOTH_BASE_URL
                }
            )
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideHttpClient(accessToken))
            .build()
    }
}



