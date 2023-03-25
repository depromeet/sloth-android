package com.depromeet.sloth.di

import android.content.Context
import com.depromeet.data.preferences.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//TODO 어떤 모듈은 object 로, 어떤 모듈은 class 로 구성 해야 하는지 학습
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

