package com.depromeet.sloth.di

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.AccessTokenAuthenticator
import com.depromeet.sloth.data.network.AuthenticationInterceptor
import com.depromeet.sloth.data.network.service.LessonService
import com.depromeet.sloth.data.network.service.LoginService
import com.depromeet.sloth.data.network.service.MemberService
import com.depromeet.sloth.data.network.service.NotificationService
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
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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
    fun provideAuthenticationInterceptor(): AuthenticationInterceptor {
        return AuthenticationInterceptor()
    }

    @Provides
    fun provideAccessTokenAuthenticator(
        preferenceManager: PreferenceManager
    ): AccessTokenAuthenticator {
        return AccessTokenAuthenticator(preferenceManager)
    }

    @Provides
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authenticationInterceptor: AuthenticationInterceptor,
        accessTokenAuthenticator: AccessTokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .authenticator(accessTokenAuthenticator)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(authenticationInterceptor)
            .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
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
    fun provideLoginService(retrofit: Retrofit): LoginService {
        return retrofit.create(LoginService::class.java)
    }

    @Singleton
    @Provides
    fun provideLessonService(retrofit: Retrofit): LessonService {
        return retrofit.create(LessonService::class.java)
    }

    @Singleton
    @Provides
    fun provideMemberService(retrofit: Retrofit): MemberService {
        return retrofit.create(MemberService::class.java)
    }

    @Singleton
    @Provides
    fun provideNotificationService(retrofit: Retrofit): NotificationService {
        return retrofit.create(NotificationService::class.java)
    }
}