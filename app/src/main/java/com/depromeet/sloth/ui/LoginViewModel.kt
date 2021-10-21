package com.depromeet.sloth.ui

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.db.PreferenceManager
import com.depromeet.sloth.data.network.*
import com.depromeet.sloth.data.network.login.LoginAccessResponse
import com.depromeet.sloth.data.network.login.LoginRepository
import com.depromeet.sloth.data.network.login.LoginResponse
import com.depromeet.sloth.data.network.login.LoginState
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class LoginViewModel: BaseViewModel() {
    private val repository = LoginRepository(ServiceGenerator())

    /**
     * Activity나 Fragment단에서 작업의 결과값을 리턴하여 State 분기를 편하게 처리할 수 있음
     *
     * @return Result<HealthResponse>
     */

    suspend fun getRefreshToken(
        accessToken: String,
        socialType: String,
        context: CoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
    ): LoginState<LoginResponse> {
        return viewModelScope.async(context = context, start = start) {
            repository.getAuthTokens(accessToken, socialType)
        }.await()
    }

    suspend fun getAccessToken(
        serverAuthCode: String,
        context: CoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
    ): LoginState<LoginAccessResponse> {
        return viewModelScope.async(context = context, start = start) {
            repository.getAccessToken(serverAuthCode)
        }.await()
    }

    suspend fun saveAuthToken(pm: PreferenceManager, accessToken: String, refreshToken: String)
    = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            pm.putAuthToken(accessToken, refreshToken)
        }
    }
}