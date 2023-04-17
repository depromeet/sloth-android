package com.depromeet.data.repository

import com.depromeet.data.source.local.UserAuthLocalDataSource
import com.depromeet.data.source.remote.UserAuthRemoteDataSource
import com.depromeet.domain.entity.LoginGoogleEntity
import com.depromeet.domain.entity.LoginSlothEntity
import com.depromeet.domain.repository.UserAuthRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class UserAuthRepositoryImpl @Inject constructor(
    private val userAuthRemoteDataSource: UserAuthRemoteDataSource,
    private val userAuthLocalDataSource: UserAuthLocalDataSource
) : UserAuthRepository {

    override suspend fun checkLoginStatus(): Boolean {
        return userAuthLocalDataSource.checkLoginStatus()
    }

    override fun googleLogin(authCode: String): Flow<Result<LoginGoogleEntity>> {
        return userAuthRemoteDataSource.googleLogin(authCode)
    }

    override fun slothLogin(authToken: String, socialType: String): Flow<Result<LoginSlothEntity>> {
        return userAuthRemoteDataSource.slothLogin(authToken, socialType)
    }

    override fun logout(): Flow<Result<String>> {
        return userAuthRemoteDataSource.logout()
    }

    override fun withdraw(): Flow<Result<String>> {
        return userAuthRemoteDataSource.withdraw()
    }

    override suspend fun registerAuthToken(accessToken: String, refreshToken: String) {
        return userAuthLocalDataSource.registerAuthToken(accessToken, refreshToken)
    }

    override suspend fun deleteAuthToken() {
        return userAuthLocalDataSource.deleteAuthToken()
    }
}

