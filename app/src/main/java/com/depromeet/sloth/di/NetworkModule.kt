package com.depromeet.sloth.di

import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.AccessTokenAuthenticator
import com.depromeet.sloth.data.network.RetrofitServiceGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * @author 최철훈
 * @created 2022-05-11
 * @desc
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideAccessTokenAuthenticator(
        preferenceManager: PreferenceManager
    ): AccessTokenAuthenticator =
        AccessTokenAuthenticator(preferenceManager)

//    @Provides
//    fun providerHttpLoggingInterceptor(): HttpLoggingInterceptor {
//        return HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
//            .apply {
//                level = HttpLoggingInterceptor.Level.BODY
//            }
//    }
//
//    @Provides
//    fun provideOkHttpClient(
//        httpLoggingInterceptor: HttpLoggingInterceptor
//    ): OkHttpClient {
//        return OkHttpClient.Builder()
//            .connectTimeout(30, TimeUnit.SECONDS)
//            .readTimeout(30, TimeUnit.SECONDS)
//            .writeTimeout(30, TimeUnit.SECONDS)
//            .addInterceptor(httpLoggingInterceptor)
//            .build()
//    }

//    @Singleton
//    @Provides
//    fun provideRetrofit(
//        okHttpClient: OkHttpClient
//    ): ServiceApi {
//        val format = Json { ignoreUnknownKeys = true }
//        val contentType = "application/json".toMediaType()
//
//        return Retrofit.Builder()
//            .client(okHttpClient)
//            .addConverterFactory(format.asConverterFactory(contentType))
//            .baseUrl(API.BASE_URL)
//            .build()
//            .create(ServiceApi::class.java)
//    }

    @Provides
    fun provideRetrofitServiceGenerator(
        accessTokenAuthenticator: AccessTokenAuthenticator
    ): RetrofitServiceGenerator =
        RetrofitServiceGenerator(accessTokenAuthenticator)
}