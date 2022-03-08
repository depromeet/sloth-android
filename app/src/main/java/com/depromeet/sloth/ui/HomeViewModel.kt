package com.depromeet.sloth.ui

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.notification.NotificationRepository
import com.depromeet.sloth.data.network.notification.NotificationSaveRequest
import com.depromeet.sloth.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : BaseViewModel() {
    suspend fun saveFCMToken(
        accessToken: String,
        notificationSaveRequest: NotificationSaveRequest
    ) = withContext(viewModelScope.coroutineContext) {
        notificationRepository.saveFCMToken(accessToken, notificationSaveRequest)
    }
}