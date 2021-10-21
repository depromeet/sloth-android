package com.depromeet.sloth.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import android.text.TextUtils
import com.depromeet.sloth.BuildConfig
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor


object ServiceGenerator {
    private const val timeoutRead = 30
    private const val timeoutConnect = 30

    private lateinit var retrofit: Retrofit
    private val httpClient = OkHttpClient.Builder()
    private val builder: Retrofit.Builder = Retrofit.Builder()

    fun setBuilderOptions(
        targetUrl: String,
        authToken: String
    ): Retrofit {
        if (!TextUtils.isEmpty(authToken)) {
            val authInterceptor = AuthenticationInterceptor(authToken)
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            }

            httpClient.apply {
                addInterceptor(loggingInterceptor)
                addInterceptor(authInterceptor)
                connectTimeout(timeoutConnect.toLong(), TimeUnit.SECONDS)
                readTimeout(timeoutRead.toLong(), TimeUnit.SECONDS)
            }

            builder.apply {
                baseUrl(targetUrl)
                addConverterFactory(GsonConverterFactory.create())
                client(httpClient.build())
            }
        }

        retrofit = builder.build()

        return retrofit
    }


    fun <S> Retrofit.createService(
        serviceClass: Class<S>
    ): S {
        return retrofit.create(serviceClass)
    }

    fun <S> createGoogleService(serviceClass: Class<S>, serverAuthCode: String): S {
        if (!TextUtils.isEmpty(serverAuthCode)) {

            httpClient.addInterceptor(Interceptor { chain ->

                val original = chain.request()
                val requestBody = FormBody.Builder()
                    .add("grant_type", "authorization_code")
                    .add("client_id", BuildConfig.GOOGLE_CLIENT_ID)
                    .add("client_secret", BuildConfig.GOOGLE_CLIENT_SECRET)
                    .add("redirect_uri", "")
                    .add("code", serverAuthCode)
                    .build()

                val request: Request = original.newBuilder()
                    .post(requestBody)
                    .build()

                chain.proceed(request)

            })

            retrofit = Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/")
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit.create(serviceClass);
    }
}