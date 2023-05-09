package com.depromeet.presentation.ui.login

import androidx.lifecycle.viewModelScope
import com.depromeet.domain.usecase.notification.RegisterNotificationTokenUseCase
import com.depromeet.domain.usecase.userauth.GoogleLoginUseCase
import com.depromeet.domain.usecase.userauth.SlothLoginUseCase
import com.depromeet.domain.util.Result
import com.depromeet.presentation.R
import com.depromeet.presentation.di.StringResourcesProvider
import com.depromeet.presentation.ui.base.BaseViewModel
import com.depromeet.presentation.util.GOOGLE
import com.depromeet.presentation.util.INTERNET_CONNECTION_ERROR
import com.depromeet.presentation.util.SERVER_CONNECTION_ERROR
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
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val slothLoginUseCase: SlothLoginUseCase,
    private val registerNotificationTokenUseCase: RegisterNotificationTokenUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
    private val messaging: FirebaseMessaging,
) : BaseViewModel() {

    private val _navigateToLoginBottomSheetEvent = MutableSharedFlow<Unit>()
    val navigateToLoginBottomSheetEvent: SharedFlow<Unit> =
        _navigateToLoginBottomSheetEvent.asSharedFlow()

    private val _navigateToRegisterBottomSheetEvent = MutableSharedFlow<Unit>()
    val navigateToRegisterBottomSheetEvent: SharedFlow<Unit> =
        _navigateToRegisterBottomSheetEvent.asSharedFlow()

    private val _registerNotificationTokenSuccessEvent = MutableSharedFlow<Unit>()
    val registerNotificationTokenSuccessEvent: SharedFlow<Unit> =
        _registerNotificationTokenSuccessEvent.asSharedFlow()

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

    fun navigateToSlothPolicyWebView() = viewModelScope.launch {
        _navigateToPrivatePolicyEvent.emit(Unit)
    }

    fun registerAgree() = viewModelScope.launch {
        _registerAgreeEvent.emit(Unit)
    }

    fun registerCancel() = viewModelScope.launch {
        _registerCancelEvent.emit(Unit)
    }

    fun googleLogin(authCode: String) = viewModelScope.launch {
        googleLoginUseCase(authCode = authCode)
            .onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        slothLogin(result.data.accessToken, GOOGLE)
                    }

                    is Result.Error -> {
                        when (result.throwable.message) {
                            SERVER_CONNECTION_ERROR -> {
                                showToast(stringResourcesProvider.getString(R.string.login_fail_by_server_error))
                            }
                            INTERNET_CONNECTION_ERROR -> {
                                showToast(stringResourcesProvider.getString(R.string.login_fail_by_internet_error))
                            }
                            else -> {
                                showToast(stringResourcesProvider.getString(R.string.login_fail))
                            }
                        }
                    }
                }
            }
    }

    fun slothLogin(accessToken: String, socialType: String) = viewModelScope.launch {
        slothLoginUseCase(authToken = accessToken, socialType = socialType)
            .onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        if (result.data.isNewUser) {
                            _navigateToRegisterBottomSheetEvent.emit(Unit)
                        } else {
                            createAndRegisterNotificationToken()
                        }
                    }
                    is Result.Error -> {
                        when (result.throwable.message) {
                            SERVER_CONNECTION_ERROR -> {
                                showToast(stringResourcesProvider.getString(R.string.login_fail_by_server_error))
                            }
                            INTERNET_CONNECTION_ERROR -> {
                                showToast(stringResourcesProvider.getString(R.string.login_fail_by_internet_error))
                            }
                            else -> {
                                showToast(stringResourcesProvider.getString(R.string.login_fail))
                            }
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
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        registerNotificationTokenSuccess()
                    }
                    is Result.Error -> {
                        when (result.throwable.message) {
                            SERVER_CONNECTION_ERROR -> {
                                showToast(stringResourcesProvider.getString(R.string.fcm_token_register_fail_by_server_error))
                            }
                            INTERNET_CONNECTION_ERROR -> {
                                showToast(stringResourcesProvider.getString(R.string.fcm_token_register_fail_by_internet_error))
                            }
                            else -> {
                                showToast(stringResourcesProvider.getString(R.string.fcm_token_register_fail))
                            }
                        }
                    }
                }
            }
    }

    private fun registerNotificationTokenSuccess() = viewModelScope.launch {
        _registerNotificationTokenSuccessEvent.emit(Unit)
    }

    override fun retry() = Unit
}