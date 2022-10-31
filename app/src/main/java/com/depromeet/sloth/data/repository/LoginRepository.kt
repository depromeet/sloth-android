package com.depromeet.sloth.data.repository

import com.depromeet.sloth.data.network.login.LoginGoogleResponse
import com.depromeet.sloth.data.network.login.LoginSlothResponse
import com.depromeet.sloth.ui.common.UiState

interface LoginRepository {

    fun checkedLoggedIn(): Boolean

    suspend fun fetchSlothAuthInfo(
        authToken: String,
        socialType: String,
    ): UiState<LoginSlothResponse>

    suspend fun fetchGoogleAuthInfo(authCode: String): UiState<LoginGoogleResponse>
}