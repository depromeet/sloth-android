package com.depromeet.sloth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.login.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
) : ViewModel() {
    suspend fun fetchSlothAuthInfo(accessToken: String, socialType: String) =
        withContext(viewModelScope.coroutineContext) {
            loginRepository.fetchSlothAuthInfo(authToken = accessToken, socialType = socialType)
        }

    suspend fun fetchGoogleAuthInfo(authCode: String) =
        withContext(viewModelScope.coroutineContext) {
            loginRepository.fetchGoogleAuthInfo(authCode = authCode)
        }
}