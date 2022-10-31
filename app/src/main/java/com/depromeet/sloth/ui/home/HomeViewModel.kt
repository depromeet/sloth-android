package com.depromeet.sloth.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.repository.MemberRepository
import com.depromeet.sloth.data.repository.NotificationRepository
import com.depromeet.sloth.data.network.notification.NotificationState
import com.depromeet.sloth.data.network.notification.fetch.NotificationFetchResponse
import com.depromeet.sloth.data.network.notification.register.NotificationRegisterRequest
import com.depromeet.sloth.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    memberRepository: MemberRepository
) : BaseViewModel(memberRepository) {

    private val _notificationFetchState =
        MutableLiveData<NotificationState<NotificationFetchResponse>>()
    val notificationFetchState: LiveData<NotificationState<NotificationFetchResponse>>
        get() = _notificationFetchState

    private val _notificationRegisterState = MutableLiveData<NotificationState<String>>()
    val notificationRegisterState: LiveData<NotificationState<String>>
        get() = _notificationRegisterState

    fun fetchFCMToken(deviceId: String) = viewModelScope.launch {
        _notificationFetchState.value = NotificationState.Loading
        _notificationFetchState.value = notificationRepository.fetchFCMToken(deviceId)
    }

    fun registerFCMToken(
        notificationRegisterRequest: NotificationRegisterRequest
    ) = viewModelScope.launch {
        _notificationRegisterState.value = NotificationState.Loading
        _notificationRegisterState.value = notificationRepository.registerFCMToken(notificationRegisterRequest)
    }
}