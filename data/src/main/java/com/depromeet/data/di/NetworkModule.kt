package com.depromeet.data.di

import com.depromeet.data.BuildConfig
import com.depromeet.data.source.local.preferences.PreferenceManager
import com.depromeet.data.service.AccessTokenAuthenticator
import com.depromeet.data.service.AccessTokenInterceptor
import com.depromeet.data.service.GoogleLoginService
import com.depromeet.data.service.LessonService
import com.depromeet.data.service.UserProfileService
import com.depromeet.data.service.NotificationService
import com.depromeet.data.service.UserAuthService
import com.depromeet.data.util.CONNECT_TIME_OUT
import com.depromeet.data.util.READ_TIME_OUT
import com.depromeet.data.util.WRITE_TIME_OUT
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@OptIn(ExperimentalSerializationApi::class)
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    internal fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
            .apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }
    }

    @Provides
    internal fun provideAccessTokenAuthenticator(
        preferences: PreferenceManager
    ): AccessTokenAuthenticator {
        return AccessTokenAuthenticator(preferences)
    }

    @Provides
    internal fun provideAuthenticationInterceptor(
        preferences: PreferenceManager
    ): AccessTokenInterceptor {
        return AccessTokenInterceptor(preferences)
    }

    @Provides
    @Named("SlothClient")
    internal fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        accessTokenAuthenticator: AccessTokenAuthenticator,
        accessTokenInterceptor: AccessTokenInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .authenticator(accessTokenAuthenticator) // To update the token when it gets HTTP unauthorized error
            .addInterceptor(accessTokenInterceptor) // To set the token in the header
            .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Named("LoginClient")
    internal fun provideOkHttpClientForLogin(
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    @Named("SlothApi")
    internal fun provideRetrofit(
        @Named("SlothClient")
        okHttpClient: OkHttpClient,
    ): Retrofit {
        val format = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(format.asConverterFactory(contentType))
            .client(okHttpClient)
            .baseUrl(BuildConfig.SLOTH_BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    @Named("GoogleLogin")
    internal fun provideRetrofitForGoogleLogin(
        @Named("LoginClient")
        okHttpClient: OkHttpClient,
    ): Retrofit {
        val format = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .addConverterFactory(format.asConverterFactory(contentType))
            .client(okHttpClient)
            .baseUrl(BuildConfig.GOOGLE_BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    @Named("SlothLogin")
    internal fun provideRetrofitForUserAuth(
        @Named("LoginClient")
        okHttpClient: OkHttpClient,
    ): Retrofit {
        val format = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(format.asConverterFactory(contentType))
            .client(okHttpClient)
            .baseUrl(BuildConfig.SLOTH_BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    internal fun provideGoogleLoginService(@Named("GoogleLogin") retrofit: Retrofit): GoogleLoginService {
        return retrofit.create(GoogleLoginService::class.java)
    }

    @Singleton
    @Provides
    internal fun provideUserAuthService(@Named("SlothLogin") retrofit: Retrofit): UserAuthService {
        return retrofit.create(UserAuthService::class.java)
    }

    @Singleton
    @Provides
    internal fun provideLessonService(@Named("SlothApi") retrofit: Retrofit): LessonService {
        return retrofit.create(LessonService::class.java)
    }

    @Singleton
    @Provides
    internal fun provideUserProfileService(@Named("SlothApi") retrofit: Retrofit): UserProfileService {
        return retrofit.create(UserProfileService::class.java)
    }

    @Singleton
    @Provides
    internal fun provideNotificationService(@Named("SlothApi") retrofit: Retrofit): NotificationService {
        return retrofit.create(NotificationService::class.java)
    }
}