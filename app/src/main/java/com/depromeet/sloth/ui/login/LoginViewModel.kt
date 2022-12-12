package com.depromeet.sloth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.data.model.response.login.LoginGoogleResponse
import com.depromeet.sloth.data.model.response.login.LoginSlothResponse
import com.depromeet.sloth.domain.use_case.login.CheckLoggedInUseCase
import com.depromeet.sloth.domain.use_case.login.GetGoogleAuthInfoUseCase
import com.depromeet.sloth.domain.use_case.login.GetSlothAuthInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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

    private val _googleLoginClick = MutableSharedFlow<Unit>()
    val googleLoginClick: SharedFlow<Unit> = _googleLoginClick.asSharedFlow()

    private val _googleLoginState = MutableSharedFlow<Result<LoginGoogleResponse>>()
    val googleLoginState: SharedFlow<Result<LoginGoogleResponse>> = _googleLoginState.asSharedFlow()

    private val _kakaoLoginClick = MutableSharedFlow<Unit>()
    val kakaoLoginClick: SharedFlow<Unit> = _kakaoLoginClick.asSharedFlow()

    private val _slothLoginState = MutableSharedFlow<Result<LoginSlothResponse>>()
    val slothLoginState: SharedFlow<Result<LoginSlothResponse>> = _slothLoginState.asSharedFlow()

    init {
        checkLoggedIn()
    }

    private fun checkLoggedIn() = viewModelScope.launch {
        _loginState.emit(checkLoggedInUseCase())
    }

    fun clickLoginBtn() = viewModelScope.launch {
        _openLoginBottomSheet.emit(Unit)
    }

    fun googleLoginClick() = viewModelScope.launch {
        _googleLoginClick.emit(Unit)
    }

    fun kakaoLoginClick() = viewModelScope.launch {
        _kakaoLoginClick.emit(Unit)
    }

    fun fetchGoogleAuthInfo(authCode: String) = viewModelScope.launch {
        getGoogleAuthInfoUseCase(authCode = authCode)
            .onEach {
                if (it is Result.Loading) _googleLoginState.emit(Result.Loading)
                else _googleLoginState.emit(Result.UnLoading)
            }.collect {
                _googleLoginState.emit(it)
            }
    }

    fun fetchSlothAuthInfo(accessToken: String, socialType: String) = viewModelScope.launch {
        getSlothAuthInfoUseCase(authToken = accessToken, socialType = socialType)
            .onEach {
                if (it is Result.Loading) _slothLoginState.emit(Result.Loading)
                else _slothLoginState.emit(Result.UnLoading)
            }.collect {
                _slothLoginState.emit(it)
            }
    }
}