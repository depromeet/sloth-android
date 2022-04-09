package com.depromeet.sloth.ui

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.data.network.notification.NotificationRepository
import com.depromeet.sloth.data.network.notification.NotificationSaveRequest
import com.depromeet.sloth.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val memberRepository: MemberRepository
) : BaseViewModel() {

    suspend fun saveFCMToken(
        notificationSaveRequest: NotificationSaveRequest
    ) = withContext(viewModelScope.coroutineContext) {
        notificationRepository.saveFCMToken(notificationSaveRequest)
    }

    fun removeAuthToken() = viewModelScope.launch {
        memberRepository.removeAuthToken()
    }

    fun putFCMToken(fcmToken: String) = viewModelScope.launch {
        memberRepository.putFCMToken(fcmToken)
    }
}