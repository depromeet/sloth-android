package com.depromeet.sloth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.domain.use_case.login.CheckLoggedInUseCase
import com.depromeet.sloth.domain.use_case.login.GetGoogleAuthInfoUseCase
import com.depromeet.sloth.domain.use_case.login.GetSlothAuthInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val checkLoggedInUseCase: CheckLoggedInUseCase,
    private val getGoogleAuthInfoUseCase: GetGoogleAuthInfoUseCase,
    private val getSlothAuthInfoUseCase: GetSlothAuthInfoUseCase,
) : ViewModel() {

    private val _loginState = MutableSharedFlow<Boolean>(replay = 1)
    val loginState: SharedFlow<Boolean> = _loginState.asSharedFlow()

    private val _openLoginBottomSheet = MutableSharedFlow<Unit>()
    val openLoginBottomSheet: SharedFlow<Unit> = _openLoginBottomSheet.asSharedFlow()

    init {
        checkLoggedIn()
    }

    private fun checkLoggedIn() = viewModelScope.launch {
        _loginState.emit(checkLoggedInUseCase())
    }

    fun clickLoginBtn() = viewModelScope.launch {
        _openLoginBottomSheet.emit(Unit)
    }

    suspend fun fetchGoogleAuthInfo(authCode: String) =
        withContext(viewModelScope.coroutineContext) {
            getGoogleAuthInfoUseCase(authCode = authCode)
        }

    suspend fun fetchSlothAuthInfo(accessToken: String, socialType: String) =
        withContext(viewModelScope.coroutineContext) {
            getSlothAuthInfoUseCase(authToken = accessToken, socialType = socialType)
        }
}