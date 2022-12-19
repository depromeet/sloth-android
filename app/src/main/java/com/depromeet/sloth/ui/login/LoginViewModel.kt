package com.depromeet.sloth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.request.notification.NotificationRegisterRequest
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.data.model.response.login.LoginGoogleResponse
import com.depromeet.sloth.data.model.response.login.LoginSlothResponse
import com.depromeet.sloth.domain.use_case.login.CheckLoggedInUseCase
import com.depromeet.sloth.domain.use_case.login.GetGoogleAuthInfoUseCase
import com.depromeet.sloth.domain.use_case.login.GetSlothAuthInfoUseCase
import com.depromeet.sloth.domain.use_case.member.RemoveAuthTokenUseCase
import com.depromeet.sloth.domain.use_case.notification.RegisterNotificationTokenUseCase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

//TODO Click 이 붙은 함수 이름 변경
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val checkLoggedInUseCase: CheckLoggedInUseCase,
    private val getGoogleAuthInfoUseCase: GetGoogleAuthInfoUseCase,
    private val getSlothAuthInfoUseCase: GetSlothAuthInfoUseCase,
    private val registerNotificationTokenUseCase: RegisterNotificationTokenUseCase,
    private val removeAuthTokenUseCase: RemoveAuthTokenUseCase,
    private val messaging: FirebaseMessaging,
) : ViewModel() {

    private val _autoLoginEvent = MutableSharedFlow<Boolean>(replay = 1)
    val autoLoginEvent: SharedFlow<Boolean> = _autoLoginEvent.asSharedFlow()

    private val _navigateToLoginBottomSheetEvent = MutableSharedFlow<Unit>()
    val navigateToLoginBottomSheetEvent: SharedFlow<Unit> = _navigateToLoginBottomSheetEvent.asSharedFlow()

    private val _googleLoginClickEvent = MutableSharedFlow<Unit>()
    val googleLoginClickEvent: SharedFlow<Unit> = _googleLoginClickEvent.asSharedFlow()

    private val _googleLoginEvent = MutableSharedFlow<Result<LoginGoogleResponse>>()
    val googleLoginEvent: SharedFlow<Result<LoginGoogleResponse>> = _googleLoginEvent.asSharedFlow()

    private val _kakaoLoginClickEvent = MutableSharedFlow<Unit>()
    val kakaoLoginClickEvent: SharedFlow<Unit> = _kakaoLoginClickEvent.asSharedFlow()

    private val _slothLoginEvent = MutableSharedFlow<Result<LoginSlothResponse>>()
    val slothLoginEvent: SharedFlow<Result<LoginSlothResponse>> = _slothLoginEvent.asSharedFlow()

    private val _registerNotificationTokenEvent = MutableSharedFlow<Result<String>>()
    val registerNotificationTokenEvent: SharedFlow<Result<String>> =
        _registerNotificationTokenEvent.asSharedFlow()

    init {
        checkLoggedIn()
    }

    private fun checkLoggedIn() = viewModelScope.launch {
        _autoLoginEvent.emit(checkLoggedInUseCase())
    }

    fun navigateToLoginBottomSheet() = viewModelScope.launch {
        _navigateToLoginBottomSheetEvent.emit(Unit)
    }

    fun googleLoginClick() = viewModelScope.launch {
        _googleLoginClickEvent.emit(Unit)
    }

    fun kakaoLoginClick() = viewModelScope.launch {
        _kakaoLoginClickEvent.emit(Unit)
    }

    fun fetchGoogleAuthInfo(authCode: String) = viewModelScope.launch {
        getGoogleAuthInfoUseCase(authCode = authCode)
            .onEach {
                if (it is Result.Loading) _googleLoginEvent.emit(Result.Loading)
                else _googleLoginEvent.emit(Result.UnLoading)
            }.collect {
                _googleLoginEvent.emit(it)
            }
    }

    fun fetchSlothAuthInfo(accessToken: String, socialType: String) = viewModelScope.launch {
        getSlothAuthInfoUseCase(authToken = accessToken, socialType = socialType)
            .onEach {
                if (it is Result.Loading) _slothLoginEvent.emit(Result.Loading)
                else _slothLoginEvent.emit(Result.UnLoading)
            }.collect {
                _slothLoginEvent.emit(it)
            }
    }

    fun createAndRegisterNotificationToken(deviceId: String) {
        messaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.w(task.exception, "Fetching FCM registration token failed")
                return@addOnCompleteListener
            }
            val fcmToken = task.result
            Timber.tag("FCM Token is created").d(fcmToken)
            registerNotificationToken(NotificationRegisterRequest(deviceId, fcmToken))
        }
    }

    private fun registerNotificationToken(
        notificationRegisterRequest: NotificationRegisterRequest
    ) = viewModelScope.launch {
        registerNotificationTokenUseCase(notificationRegisterRequest)
            .onEach {
                if (it is Result.Loading) _registerNotificationTokenEvent.emit(Result.Loading)
                else _registerNotificationTokenEvent.emit(Result.UnLoading)
            }.collect {
                _registerNotificationTokenEvent.emit(it)
            }
    }

    fun removeAuthToken() = viewModelScope.launch {
        removeAuthTokenUseCase()
    }
}