package com.depromeet.sloth.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.notification.fetch.NotificationFetchResponse
import com.depromeet.sloth.data.network.notification.register.NotificationRegisterRequest
import com.depromeet.sloth.data.repository.MemberRepository
import com.depromeet.sloth.data.repository.NotificationRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    memberRepository: MemberRepository
) : BaseViewModel(memberRepository) {

    private val _notificationFetchState =
        MutableLiveData<UiState<NotificationFetchResponse>>()
    val notificationFetchState: LiveData<UiState<NotificationFetchResponse>>
        get() = _notificationFetchState

    private val _notificationRegisterState = MutableLiveData<UiState<String>>()
    val notificationRegisterState: LiveData<UiState<String>>
        get() = _notificationRegisterState

    fun fetchFCMToken(deviceId: String) = viewModelScope.launch {
        _notificationFetchState.value = UiState.Loading
        _notificationFetchState.value = notificationRepository.fetchFCMToken(deviceId)
    }

    fun registerFCMToken(
        notificationRegisterRequest: NotificationRegisterRequest
    ) = viewModelScope.launch {
        _notificationRegisterState.value = UiState.Loading
        _notificationRegisterState.value = notificationRepository.registerFCMToken(notificationRegisterRequest)
    }
}