package com.depromeet.sloth.ui

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.db.PreferenceManager
import com.depromeet.sloth.data.network.login.LoginAccessResponse
import com.depromeet.sloth.data.network.login.LoginRepository
import com.depromeet.sloth.data.network.login.LoginResponse
import com.depromeet.sloth.data.network.login.LoginState
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class LoginViewModel : BaseViewModel() {
    private val loginRepository = LoginRepository()

    suspend fun getAuthInfo(
        accessToken: String,
        socialType: String,
        context: CoroutineContext = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT,
    ): LoginState<LoginResponse> = viewModelScope.async(
        context = context,
        start = start
    ) {
        loginRepository.login(
            accessToken = accessToken,
            socialType = socialType
        )
    }.await()

    suspend fun getAccessToken(
        serverAuthCode: String,
        context: CoroutineContext = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT,
    ): LoginState<LoginAccessResponse> = viewModelScope.async(
        context = context,
        start = start
    ) {
        loginRepository.getAccessToken(
            serverAuthCode = serverAuthCode
        )
    }.await()

    suspend fun getRefreshToken(
        accessToken: String,
        socialType: String,
        context: CoroutineContext = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT,
    ): LoginState<LoginResponse> = viewModelScope.async(
        context = context,
        start = start
    ) {
        loginRepository.getAuthTokens(
            accessToken = accessToken,
            socialType = socialType
        )
    }.await()

    suspend fun saveAuthToken(pm: PreferenceManager, accessToken: String, refreshToken: String) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                pm.putAuthToken(accessToken, refreshToken)
            }
        }
}