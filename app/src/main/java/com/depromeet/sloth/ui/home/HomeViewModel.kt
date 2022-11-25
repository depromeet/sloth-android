package com.depromeet.sloth.ui.home

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.common.Result
import com.depromeet.sloth.data.model.request.notification.NotificationRegisterRequest
import com.depromeet.sloth.data.model.response.notification.NotificationFetchResponse
import com.depromeet.sloth.data.repository.MemberRepository
import com.depromeet.sloth.data.repository.NotificationRepository
import com.depromeet.sloth.ui.base.BaseViewModel
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
    private val notificationRepository: NotificationRepository,
    private val messaging: FirebaseMessaging,
    memberRepository: MemberRepository
) : BaseViewModel(memberRepository) {

    private val _notificationFetchState =
        MutableSharedFlow<Result<NotificationFetchResponse>>()
    val notificationFetchState: SharedFlow<Result<NotificationFetchResponse>> =
        _notificationFetchState.asSharedFlow()

    private val _notificationRegisterState = MutableSharedFlow<Result<String>>()
    val notificationRegisterState: SharedFlow<Result<String>> =
        _notificationRegisterState.asSharedFlow()

    fun fetchFCMToken(deviceId: String) = viewModelScope.launch {
        notificationRepository.fetchFCMToken(deviceId)
            .onEach {
                if (it is Result.Loading) _notificationFetchState.emit(Result.Loading)
                else _notificationFetchState.emit(Result.UnLoading)
            }.collect {
                _notificationFetchState.emit(it)
            }
    }

    fun createAndRegisterFCMToken(deviceId: String) {
        messaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.w(task.exception, "Fetching FCM registration token failed")
                return@addOnCompleteListener
            }
            val fcmToken = task.result
            Timber.tag("FCM Token is created").d(fcmToken)
            registerFCMToken(NotificationRegisterRequest(deviceId, fcmToken))
        }
    }

    private fun registerFCMToken(
        notificationRegisterRequest: NotificationRegisterRequest
    ) = viewModelScope.launch {
        notificationRepository.registerFCMToken(notificationRegisterRequest)
            .onEach {
                if (it is Result.Loading) _notificationRegisterState.emit(Result.Loading)
                else _notificationRegisterState.emit(Result.UnLoading)
            }.collect {
                _notificationRegisterState.emit(it)
            }
    }
}