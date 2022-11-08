package com.depromeet.sloth.data.repository

import com.depromeet.sloth.data.model.response.login.LoginGoogleResponse
import com.depromeet.sloth.data.model.response.login.LoginSlothResponse
import com.depromeet.sloth.common.Result

interface LoginRepository {

    fun checkedLoggedIn(): Boolean

    suspend fun fetchSlothAuthInfo(
        authToken: String,
        socialType: String,
    ): Result<LoginSlothResponse>

    suspend fun fetchGoogleAuthInfo(authCode: String): Result<LoginGoogleResponse>
}