package com.depromeet.sloth.data.network.login

interface LoginRepository {

    suspend fun fetchSlothAuthInfo(
        authToken: String,
        socialType: String,
    ): LoginState<LoginSlothResponse>

    suspend fun fetchGoogleAuthInfo(authCode: String): LoginState<LoginGoogleResponse>
}