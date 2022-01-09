package com.depromeet.sloth.ui.login

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.login.LoginGoogleResponse
import com.depromeet.sloth.data.network.login.LoginRepository
import com.depromeet.sloth.data.network.login.LoginSlothResponse
import com.depromeet.sloth.data.network.login.LoginState
import com.depromeet.sloth.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
): BaseViewModel() {
    suspend fun fetchSlothAuthInfo(
        accessToken: String,
        socialType: String,
        context: CoroutineContext = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT,
    ): LoginState<LoginSlothResponse> = viewModelScope.async(
        context = context,
        start = start
    ) {
        loginRepository.fetchSlothAuthInfo(
            accessToken = accessToken,
            socialType = socialType
        )
    }.await()

    suspend fun fetchGoogleAuthInfo(
        authCode: String,
        context: CoroutineContext = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT,
    ): LoginState<LoginGoogleResponse> = viewModelScope.async(
        context = context,
        start = start
    ) {
        loginRepository.fetchGoogleAuthInfo(
            authCode = authCode
        )
    }.await()
}