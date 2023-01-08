package com.depromeet.sloth.ui.login

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.response.login.LoginSlothResponse
import com.depromeet.sloth.domain.use_case.login.GetGoogleAuthInfoUseCase
import com.depromeet.sloth.domain.use_case.login.GetSlothAuthInfoUseCase
import com.depromeet.sloth.domain.use_case.member.RemoveAuthTokenUseCase
import com.depromeet.sloth.domain.use_case.notification.RegisterNotificationTokenUseCase
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.util.GOOGLE
import com.depromeet.sloth.util.Result
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getGoogleAuthInfoUseCase: GetGoogleAuthInfoUseCase,
    private val getSlothAuthInfoUseCase: GetSlothAuthInfoUseCase,
    private val registerNotificationTokenUseCase: RegisterNotificationTokenUseCase,
    private val removeAuthTokenUseCase: RemoveAuthTokenUseCase,
    private val messaging: FirebaseMessaging,
) : BaseViewModel() {

    private val _googleLoginFail = MutableSharedFlow<Unit>()
    val googleLoginFail: SharedFlow<Unit> = _googleLoginFail.asSharedFlow()

    private val _loginSuccess = MutableSharedFlow<LoginSlothResponse>()
    val loginSuccess: SharedFlow<LoginSlothResponse> = _loginSuccess.asSharedFlow()

    private val _loginFail = MutableSharedFlow<Unit>()
    val loginFail: SharedFlow<Unit> = _loginFail.asSharedFlow()

    private val _registerNotificationTokenSuccess = MutableSharedFlow<Unit>()
    val registerNotificationTokenSuccess: SharedFlow<Unit> = _registerNotificationTokenSuccess.asSharedFlow()

    private val _registerNotificationTokenFail = MutableSharedFlow<Int>()
    val registerNotificationTokenFail: SharedFlow<Int> = _registerNotificationTokenFail.asSharedFlow()

    private val _navigateToLoginBottomSheetEvent = MutableSharedFlow<Unit>()
    val navigateToLoginBottomSheetEvent: SharedFlow<Unit> =
        _navigateToLoginBottomSheetEvent.asSharedFlow()

    private val _googleLoginEvent = MutableSharedFlow<Unit>()
    val googleLoginEvent: SharedFlow<Unit> = _googleLoginEvent.asSharedFlow()

    private val _kakaoLoginEvent = MutableSharedFlow<Unit>()
    val kakaoLoginEvent: SharedFlow<Unit> = _kakaoLoginEvent.asSharedFlow()

    private val _navigateToPrivatePolicyEvent = MutableSharedFlow<Unit>()
    val navigateToPrivatePolicyEvent: SharedFlow<Unit> =
        _navigateToPrivatePolicyEvent.asSharedFlow()

    private val _registerAgreeEvent = MutableSharedFlow<Unit>()
    val registerAgreeEvent: SharedFlow<Unit> = _registerAgreeEvent.asSharedFlow()

    private val _registerCancelEvent = MutableSharedFlow<Unit>()
    val registerCancelEvent: SharedFlow<Unit> = _registerCancelEvent.asSharedFlow()

    fun navigateToLoginBottomSheet() = viewModelScope.launch {
        _navigateToLoginBottomSheetEvent.emit(Unit)
    }

    fun googleLogin() = viewModelScope.launch {
        _googleLoginEvent.emit(Unit)
    }

    fun kakaoLogin() = viewModelScope.launch {
        _kakaoLoginEvent.emit(Unit)
    }

    fun navigateToSlothPolicyWebview() = viewModelScope.launch {
        _navigateToPrivatePolicyEvent.emit(Unit)
    }

    fun registerAgree() = viewModelScope.launch {
        _registerAgreeEvent.emit(Unit)
    }

    fun registerCancel() = viewModelScope.launch {
        _registerCancelEvent.emit(Unit)
    }

    fun fetchGoogleAuthInfo(authCode: String) = viewModelScope.launch {
        getGoogleAuthInfoUseCase(authCode = authCode)
            .onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        fetchSlothAuthInfo(result.data.accessToken, GOOGLE)
                    }
                    is Result.Error ->
                        _googleLoginFail.emit(Unit)
                }
            }
    }

    fun fetchSlothAuthInfo(accessToken: String, socialType: String) = viewModelScope.launch {
        getSlothAuthInfoUseCase(authToken = accessToken, socialType = socialType)
            .onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        _loginSuccess.emit(result.data)
                    }
                    is Result.Error -> {
                        _loginFail.emit(Unit)
                    }
                }
            }
    }

    fun createAndRegisterNotificationToken() {
        messaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.w(task.exception, "Fetching FCM registration token failed")
                return@addOnCompleteListener
            }
            val fcmToken = task.result
            Timber.tag("FCM Token is created").d(fcmToken)
            registerNotificationToken(fcmToken)
        }
    }

    private fun registerNotificationToken(
        fcmToken: String
    ) = viewModelScope.launch {
        registerNotificationTokenUseCase(fcmToken)
            .onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when(result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> _registerNotificationTokenSuccess.emit(Unit)
                    is Result.Error -> result.statusCode?.let {_registerNotificationTokenFail.emit(it) }
                }
            }
    }

    fun removeAuthToken() = viewModelScope.launch {
        removeAuthTokenUseCase()
    }

    override fun retry() = Unit
}