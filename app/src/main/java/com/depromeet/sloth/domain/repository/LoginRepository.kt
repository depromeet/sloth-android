package com.depromeet.sloth.domain.repository

import com.depromeet.sloth.data.model.response.login.LoginGoogleResponse
import com.depromeet.sloth.data.model.response.login.LoginSlothResponse
import com.depromeet.sloth.util.Result
import kotlinx.coroutines.flow.Flow

interface LoginRepository {

    suspend fun checkLoggedIn(): Boolean

    fun fetchSlothAuthInfo(
        authToken: String,
        socialType: String,
    ): Flow<Result<LoginSlothResponse>>

    fun fetchGoogleAuthInfo(authCode: String): Flow<Result<LoginGoogleResponse>>
}