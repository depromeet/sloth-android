package com.depromeet.sloth.ui.home

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.notification.fetch.NotificationFetchResponse
import com.depromeet.sloth.data.network.notification.register.NotificationRegisterRequest
import com.depromeet.sloth.data.repository.MemberRepository
import com.depromeet.sloth.data.repository.NotificationRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.common.UiState
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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
        MutableSharedFlow<UiState<NotificationFetchResponse>>()
    val notificationFetchState: SharedFlow<UiState<NotificationFetchResponse>> =
        _notificationFetchState.asSharedFlow()

    private val _notificationRegisterState = MutableSharedFlow<UiState<String>>()
    val notificationRegisterState: SharedFlow<UiState<String>> =
        _notificationRegisterState.asSharedFlow()

    fun fetchFCMToken(deviceId: String) = viewModelScope.launch {
        _notificationFetchState.emit(UiState.Loading)
        _notificationFetchState.emit(notificationRepository.fetchFCMToken(deviceId))
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
        _notificationRegisterState.emit(UiState.Loading)
        _notificationRegisterState.emit(
            notificationRepository.registerFCMToken(
                notificationRegisterRequest
            )
        )
    }
}