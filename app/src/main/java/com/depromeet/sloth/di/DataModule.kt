package com.depromeet.sloth.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.depromeet.sloth.data.preferences.Preferences
import com.depromeet.sloth.data.preferences.PreferencesImpl
import com.depromeet.sloth.util.KEY_PREFERENCES
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

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
}