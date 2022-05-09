package com.depromeet.sloth.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.data.network.notification.NotificationRepository
import com.depromeet.sloth.data.network.notification.NotificationRegisterRequest
import com.depromeet.sloth.data.network.notification.NotificationRegisterState
import com.depromeet.sloth.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    val memberRepository: MemberRepository
) : BaseViewModel(memberRepository) {

    private val _notificationRegisterState = MutableLiveData<NotificationRegisterState<String>>()
    val notificationRegisterState: LiveData<NotificationRegisterState<String>> = _notificationRegisterState

    fun registerFCMToken(
        notificationRegisterRequest: NotificationRegisterRequest
    ) = viewModelScope.launch {
        _notificationRegisterState.value = NotificationRegisterState.Loading
        val notificationRegisterResponse = notificationRepository.registerFCMToken(notificationRegisterRequest)
        _notificationRegisterState.value = notificationRegisterResponse
    }

    fun putFCMToken(fcmToken: String) = viewModelScope.launch {
        memberRepository.putFCMToken(fcmToken)
    }
}