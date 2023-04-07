package com.depromeet.data.source.local

interface UserAuthLocalDataSource {

    suspend fun checkLoginStatus(): Boolean

    suspend fun registerAuthToken(accessToken: String, refreshToken: String)

    suspend fun deleteAuthToken()
}