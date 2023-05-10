package com.depromeet.data.di

import com.depromeet.data.util.FileManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object FileManagerModule {

    @Provides
    @Singleton
    fun provideFileManager(): FileManager {
        return FileManager
    }
}