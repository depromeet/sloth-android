package com.depromeet.sloth.di

import android.content.Context
import com.depromeet.sloth.data.preferences.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun providePreferenceManager(@ApplicationContext context: Context) = PreferenceManager(context)

//    @Provides
//    @Singleton
//    fun provideSharedPreferences(
//        app: Application
//    ): SharedPreferences {
//        return app.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE)
//    }

//    @Provides
//    @Singleton
//    fun providePreferences(
//        sharedPreferences: SharedPreferences
//    ): Preferences {
//        return PreferencesImpl(sharedPreferences)
//    }
}

