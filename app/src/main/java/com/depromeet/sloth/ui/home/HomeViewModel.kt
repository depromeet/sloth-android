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

    private val _fetchNotificationTokenEvent =
        MutableSharedFlow<Result<NotificationFetchResponse>>()
    val fetchNotificationTokenEvent: SharedFlow<Result<NotificationFetchResponse>> =
        _fetchNotificationTokenEvent.asSharedFlow()

    private val _registerNotificationTokenEvent = MutableSharedFlow<Result<String>>()
    val registerNotificationTokenEvent: SharedFlow<Result<String>> =
        _registerNotificationTokenEvent.asSharedFlow()

    fun fetchNotificationToken(deviceId: String) = viewModelScope.launch {
        getNotificationTokenUseCase(deviceId)
            .onEach {
                if (it is Result.Loading) _fetchNotificationTokenEvent.emit(Result.Loading)
                else _fetchNotificationTokenEvent.emit(Result.UnLoading)
            }.collect {
                _fetchNotificationTokenEvent.emit(it)
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