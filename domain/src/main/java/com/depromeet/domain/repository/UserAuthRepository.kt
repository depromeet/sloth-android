package com.depromeet.domain.repository

import com.depromeet.domain.entity.LoginGoogleEntity
import com.depromeet.domain.entity.LoginSlothEntity
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow


interface UserAuthRepository {

    suspend fun checkLoginStatus(): Boolean

    fun slothLogin(
        authToken: String,
        socialType: String,
    ): Flow<Result<LoginSlothEntity>>

    fun googleLogin(authCode: String): Flow<Result<LoginGoogleEntity>>

    fun logout(): Flow<Result<String>>

    fun withdraw(): Flow<Result<String>>

    suspend fun registerAuthToken(accessToken: String, refreshToken: String)

    suspend fun deleteAuthToken()
}