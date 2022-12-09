package com.depromeet.sloth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.domain.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
) : ViewModel() {

    private val _loginState = MutableSharedFlow<Boolean>(replay = 1)
    val loginState: SharedFlow<Boolean>
        get() = _loginState

    private val _openLoginBottomSheetEvent = MutableSharedFlow<Unit>()
    val openLoginBottomSheetEvent: SharedFlow<Unit>
        get() = _openLoginBottomSheetEvent

    init {
        checkLoggedIn()
    }

    private fun checkLoggedIn() = viewModelScope.launch {
        _loginState.emit(loginRepository.checkLoggedIn())
    }

    fun clickLoginBtn() = viewModelScope.launch {
        _openLoginBottomSheetEvent.emit(Unit)
    }

    suspend fun fetchSlothAuthInfo(accessToken: String, socialType: String) =
        withContext(viewModelScope.coroutineContext) {
            loginRepository.fetchSlothAuthInfo(authToken = accessToken, socialType = socialType)
        }

    suspend fun fetchGoogleAuthInfo(authCode: String) =
        withContext(viewModelScope.coroutineContext) {
            loginRepository.fetchGoogleAuthInfo(authCode = authCode)
        }
}