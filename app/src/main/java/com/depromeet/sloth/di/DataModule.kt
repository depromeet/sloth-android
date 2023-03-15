package com.depromeet.sloth.di

import android.content.Context
import com.depromeet.data.preferences.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//TODO 어떤건 object 로 해야하고 어떤건 class 로 해야 구성해야하는지
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

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

