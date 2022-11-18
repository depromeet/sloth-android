package com.depromeet.sloth.di

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.network.AccessTokenAuthenticator
import com.depromeet.sloth.data.network.AuthenticationInterceptor
import com.depromeet.sloth.data.network.service.*
import com.depromeet.sloth.data.preferences.Preferences
import com.depromeet.sloth.util.CONNECT_TIME_OUT
import com.depromeet.sloth.util.READ_TIME_OUT
import com.depromeet.sloth.util.WRITE_TIME_OUT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
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
    fun provideAccessTokenAuthenticator(
        preferences: Preferences
    ): AccessTokenAuthenticator {
        return AccessTokenAuthenticator(preferences)
    }

    @Provides
    fun provideAuthenticationInterceptor(
        preferences: Preferences
    ): AuthenticationInterceptor {
        return AuthenticationInterceptor(preferences)
    }

    @Provides
    @Named("SlothClient")
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        accessTokenAuthenticator: AccessTokenAuthenticator,
        authenticationInterceptor: AuthenticationInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .authenticator(accessTokenAuthenticator) // To update the token when it gets HTTP unauthorized error
            .addInterceptor(authenticationInterceptor) // To set the token in the header
            .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Named("LoginClient")
    fun provideOkHttpClientForLogin(
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
    fun provideRetrofit(
        @Named("SlothClient")
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(BuildConfig.SLOTH_BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    @Named("GoogleLogin")
    fun provideRetrofitForGoogleLogin(
        @Named("LoginClient")
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(BuildConfig.GOOGLE_BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    @Named("SlothLogin")
    fun provideRetrofitForSlothLogin(
        @Named("LoginClient")
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(BuildConfig.SLOTH_BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    fun provideGoogleLoginService(@Named("GoogleLogin")retrofit: Retrofit): GoogleLoginService {
        return retrofit.create(GoogleLoginService::class.java)
    }

    @Singleton
    @Provides
    fun provideSlothLoginService(@Named("SlothLogin")retrofit: Retrofit): SlothLoginService {
        return retrofit.create(SlothLoginService::class.java)
    }

    @Singleton
    @Provides
    fun provideLessonService(@Named("SlothApi")retrofit: Retrofit): LessonService {
        return retrofit.create(LessonService::class.java)
    }

    @Singleton
    @Provides
    fun provideMemberService(@Named("SlothApi")retrofit: Retrofit): MemberService {
        return retrofit.create(MemberService::class.java)
    }

    @Singleton
    @Provides
    fun provideNotificationService(@Named("SlothApi")retrofit: Retrofit): NotificationService {
        return retrofit.create(NotificationService::class.java)
    }
}