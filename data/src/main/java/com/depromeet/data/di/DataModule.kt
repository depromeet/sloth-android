package com.depromeet.data.di

import android.content.Context
import com.depromeet.data.source.local.UserAuthLocalDataSource
import com.depromeet.data.source.local.UserAuthLocalDataSourceImpl
import com.depromeet.data.source.local.preferences.PreferenceManager
import com.depromeet.data.source.remote.LessonRemoteDataSource
import com.depromeet.data.source.remote.LessonRemoteDataSourceImpl
import com.depromeet.data.source.remote.NotificationRemoteDataSource
import com.depromeet.data.source.remote.NotificationRemoteDataSourceImpl
import com.depromeet.data.source.remote.UserAuthRemoteDataSource
import com.depromeet.data.source.remote.UserAuthRemoteDataSourceImpl
import com.depromeet.data.source.remote.UserProfileRemoteDataSource
import com.depromeet.data.source.remote.UserProfileRemoteDataSourceImpl
import com.depromeet.data.service.GoogleLoginService
import com.depromeet.data.service.LessonService
import com.depromeet.data.service.UserProfileService
import com.depromeet.data.service.NotificationService
import com.depromeet.data.service.UserAuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    internal fun provideLessonRemoteDataSource(
        lessonService: LessonService,
        preferences: PreferenceManager
    ): LessonRemoteDataSource {
        return LessonRemoteDataSourceImpl(lessonService, preferences)
    }

    @Singleton
    @Provides
    internal fun provideUserAuthRemoteDataSource(
        googleLoginService: GoogleLoginService,
        userAuthService: UserAuthService,
        preferences: PreferenceManager
    ): UserAuthRemoteDataSource {
        return UserAuthRemoteDataSourceImpl(userAuthService, googleLoginService, preferences)
    }

    @Singleton
    @Provides
    internal fun provideUserProfileRemoteDataSource(
        @ApplicationContext context: Context,
        userProfileService: UserProfileService,
        preferences: PreferenceManager
    ): UserProfileRemoteDataSource {
        return UserProfileRemoteDataSourceImpl(context, userProfileService, preferences)
    }

    @Singleton
    @Provides
    internal fun provideNotificationRemoteDataSource(
        @ApplicationContext context: Context,
        notificationService: NotificationService,
        preferences: PreferenceManager
    ): NotificationRemoteDataSource {
        return NotificationRemoteDataSourceImpl(context, notificationService, preferences)
    }

    @Singleton
    @Provides
    internal fun provideUserAuthLocalDataSource(preferenceManager: PreferenceManager): UserAuthLocalDataSource {
        return UserAuthLocalDataSourceImpl(preferenceManager)
    }

    @Singleton
    @Provides
    internal fun providePreferenceManager(@ApplicationContext context: Context) = PreferenceManager(context)

//    @Singleton
//    @Provides
//    fun provideSharedPreferences(
//        app: Application
//    ): SharedPreferences {
//        return app.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE)
//    }

//    @Singleton
//    @Provides
//    fun providePreferences(
//        sharedPreferences: SharedPreferences
//    ): Preferences {
//        return PreferencesImpl(sharedPreferences)
//    }
}

