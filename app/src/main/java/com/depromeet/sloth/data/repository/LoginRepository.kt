package com.depromeet.sloth.data.repository

import com.depromeet.sloth.data.network.login.LoginGoogleResponse
import com.depromeet.sloth.data.network.login.LoginSlothResponse
import com.depromeet.sloth.data.network.login.LoginState

interface LoginRepository {

    suspend fun fetchSlothAuthInfo(
        authToken: String,
        socialType: String,
    ): LoginState<LoginSlothResponse>

    suspend fun fetchGoogleAuthInfo(authCode: String): LoginState<LoginGoogleResponse>
}