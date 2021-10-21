package com.depromeet.sloth.data.network

import com.depromeet.sloth.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import android.text.TextUtils
import okhttp3.FormBody
import okhttp3.Request

private const val timeoutRead = 30   //In seconds
private const val contentType = "Content-Type"
private const val contentTypeValue = "application/json"
private const val timeoutConnect = 30   //In seconds

class ServiceGenerator {

    private val okHttpBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
    lateinit var retrofit: Retrofit

    private val logger: HttpLoggingInterceptor
        get() {
            val loggingInterceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                loggingInterceptor.apply { level = HttpLoggingInterceptor.Level.BODY }
            }
            return loggingInterceptor
        }

    init {
        okHttpBuilder.addInterceptor(logger)
        okHttpBuilder.connectTimeout(timeoutConnect.toLong(), TimeUnit.SECONDS)
        okHttpBuilder.readTimeout(timeoutRead.toLong(), TimeUnit.SECONDS)
    }

    fun <S> createService(serviceClass: Class<S>): S {
        retrofit = Retrofit.Builder()
            .baseUrl("https://13.124.140.7")
            .client(okHttpBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(serviceClass)
    }

    fun <S> createService(serviceClass: Class<S>, accessToken: String): S {
        if (!TextUtils.isEmpty(accessToken)) {

            okHttpBuilder.addInterceptor(Interceptor { chain ->

                val original = chain.request()
                val request = original.newBuilder()
                    .header("Authorization", accessToken)
                    .header(contentType, contentTypeValue)
                    .build()

                chain.proceed(request)
            })

            retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.SLOTH_BASE_URL)
                .client(okHttpBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit.create(serviceClass)
    }

    fun <S> createGoogleService(serviceClass: Class<S>, serverAuthCode: String): S {
        if (!TextUtils.isEmpty(serverAuthCode)) {

            okHttpBuilder.addInterceptor(Interceptor { chain ->

                val original = chain.request()
                val requestBody = FormBody.Builder()
                    .add("grant_type", "authorization_code")
                    .add(
                        "client_id",
                        BuildConfig.GOOGLE_CLIENT_ID
                    )
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
                .client(okHttpBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit.create(serviceClass);
    }
}
