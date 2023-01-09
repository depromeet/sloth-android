package com.depromeet.sloth.ui.login

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.use_case.login.GetGoogleAuthInfoUseCase
import com.depromeet.sloth.domain.use_case.login.GetSlothAuthInfoUseCase
import com.depromeet.sloth.domain.use_case.member.RemoveAuthTokenUseCase
import com.depromeet.sloth.domain.use_case.notification.RegisterNotificationTokenUseCase
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.util.GOOGLE
import com.depromeet.sloth.util.INTERNET_CONNECTION_ERROR
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
    private val stringResourcesProvider: StringResourcesProvider,
    private val messaging: FirebaseMessaging,
) : BaseViewModel() {

    private val _registerNotificationTokenSuccess = MutableSharedFlow<Unit>()
    val registerNotificationTokenSuccess: SharedFlow<Unit> = _registerNotificationTokenSuccess.asSharedFlow()

    private val _navigateToLoginBottomSheetEvent = MutableSharedFlow<Unit>()
    val navigateToLoginBottomSheetEvent: SharedFlow<Unit> =
        _navigateToLoginBottomSheetEvent.asSharedFlow()

    private val _navigateToRegisterBottomSheetEvent = MutableSharedFlow<Unit>()
    val navigateToRegisterBottomSheetEvent: SharedFlow<Unit> =
        _navigateToRegisterBottomSheetEvent.asSharedFlow()

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
                showLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        fetchSlothAuthInfo(result.data.accessToken, GOOGLE)
                    }
                    is Result.Error -> {
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            showToastEvent(stringResourcesProvider.getString(R.string.login_fail_by_internet_error))
                        } else {
                            showToastEvent(stringResourcesProvider.getString(R.string.login_fail))
                        }
                    }
                }
            }
    }

    fun fetchSlothAuthInfo(accessToken: String, socialType: String) = viewModelScope.launch {
        getSlothAuthInfoUseCase(authToken = accessToken, socialType = socialType)
            .onEach { result ->
                showLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        if (result.data.isNewMember) {
                            _navigateToRegisterBottomSheetEvent.emit(Unit)
                        } else {
                            createAndRegisterNotificationToken()
                        }
                    }
                    is Result.Error -> {
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            showToastEvent(stringResourcesProvider.getString(R.string.login_fail_by_internet_error))
                        } else {
                            showToastEvent(stringResourcesProvider.getString(R.string.login_fail))
                        }
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
                showLoading(result is Result.Loading)
            }.collect { result ->
                when(result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        _registerNotificationTokenSuccess.emit(Unit)
                    }
                    is Result.Error -> {
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            showToastEvent(stringResourcesProvider.getString(R.string.please_check_internet))
                        } else {
                            showToastEvent(stringResourcesProvider.getString(R.string.please_check_internet))
                        }
                    }
                }
            }
    }

    fun removeAuthToken() = viewModelScope.launch {
        removeAuthTokenUseCase()
    }

    override fun retry() = Unit
}