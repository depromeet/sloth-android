package com.depromeet.sloth.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.network.AccessTokenAuthenticator
import com.depromeet.sloth.data.network.AuthenticationInterceptor
import com.depromeet.sloth.data.network.service.*
import com.depromeet.sloth.data.preferences.Preferences
import com.depromeet.sloth.data.preferences.PreferencesImpl
import com.depromeet.sloth.util.CONNECT_TIME_OUT
import com.depromeet.sloth.util.KEY_PREFERENCES
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
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(
        app: Application
    ): SharedPreferences {
        return app.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providePreferences(
        sharedPreferences: SharedPreferences
    ): Preferences {
        return PreferencesImpl(sharedPreferences)
    }

    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
            .apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BODY
                } else {
                    level = HttpLoggingInterceptor.Level.NONE
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
    @Named("Sloth")
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
    @Named("Login")
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
    @Named("Sloth")
    fun provideRetrofit(
        @Named("Sloth")
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
        @Named("Login")
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
        @Named("Login")
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
    fun provideLessonService(@Named("Sloth")retrofit: Retrofit): LessonService {
        return retrofit.create(LessonService::class.java)
    }

    @Singleton
    @Provides
    fun provideMemberService(@Named("Sloth")retrofit: Retrofit): MemberService {
        return retrofit.create(MemberService::class.java)
    }

    @Singleton
    @Provides
    fun provideNotificationService(@Named("Sloth")retrofit: Retrofit): NotificationService {
        return retrofit.create(NotificationService::class.java)
    }
}