package com.depromeet.sloth.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.data.model.request.notification.NotificationRegisterRequest
import com.depromeet.sloth.data.model.response.notification.NotificationFetchResponse
import com.depromeet.sloth.domain.use_case.member.RemoveAuthTokenUseCase
import com.depromeet.sloth.domain.use_case.notification.GetNotificationTokenUseCase
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

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getNotificationTokenUseCase: GetNotificationTokenUseCase,
    private val registerNotificationTokenUseCase: RegisterNotificationTokenUseCase,
    private val removeAuthTokenUseCase: RemoveAuthTokenUseCase,
    private val messaging: FirebaseMessaging,
) : ViewModel() {

    private val _notificationFetchState =
        MutableSharedFlow<Result<NotificationFetchResponse>>()
    val notificationFetchState: SharedFlow<Result<NotificationFetchResponse>> =
        _notificationFetchState.asSharedFlow()

    private val _notificationRegisterState = MutableSharedFlow<Result<String>>()
    val notificationRegisterState: SharedFlow<Result<String>> =
        _notificationRegisterState.asSharedFlow()

    fun fetchNotificationToken(deviceId: String) = viewModelScope.launch {
        getNotificationTokenUseCase(deviceId)
            .onEach {
                if (it is Result.Loading) _notificationFetchState.emit(Result.Loading)
                else _notificationFetchState.emit(Result.UnLoading)
            }.collect {
                _notificationFetchState.emit(it)
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
                if (it is Result.Loading) _notificationRegisterState.emit(Result.Loading)
                else _notificationRegisterState.emit(Result.UnLoading)
            }.collect {
                _notificationRegisterState.emit(it)
            }
    }

    fun removeAuthToken() = viewModelScope.launch {
        removeAuthTokenUseCase()
    }
}