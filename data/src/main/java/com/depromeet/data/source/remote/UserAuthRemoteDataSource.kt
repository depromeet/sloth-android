package com.depromeet.data.source.remote

import com.depromeet.domain.entity.LoginGoogleEntity
import com.depromeet.domain.entity.LoginSlothEntity
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface UserAuthRemoteDataSource {

    suspend fun fetchLoginStatus(): Boolean

    fun fetchSlothAuthInfo(
        authToken: String,
        socialType: String,
    ): Flow<Result<LoginSlothEntity>>

    fun fetchGoogleAuthInfo(authCode: String): Flow<Result<LoginGoogleEntity>>

    fun logout(): Flow<Result<String>>
}