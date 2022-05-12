package com.depromeet.sloth.di

import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.AccessTokenAuthenticator
import com.depromeet.sloth.data.network.RetrofitServiceGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * @author 최철훈
 * @created 2022-05-11
 * @desc
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideAccessTokenAuthenticator(
        preferenceManager: PreferenceManager
    ): AccessTokenAuthenticator =
        AccessTokenAuthenticator(preferenceManager)

    @Provides
    fun provideRetrofitServiceGenerator(
        accessTokenAuthenticator: AccessTokenAuthenticator
    ): RetrofitServiceGenerator =
        RetrofitServiceGenerator(accessTokenAuthenticator)
}