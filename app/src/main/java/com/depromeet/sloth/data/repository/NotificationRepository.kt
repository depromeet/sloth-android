package com.depromeet.sloth.data.repository

import com.depromeet.sloth.data.network.notification.fetch.NotificationFetchResponse
import com.depromeet.sloth.data.network.notification.register.NotificationRegisterRequest
import com.depromeet.sloth.data.network.notification.update.NotificationUpdateRequest
import com.depromeet.sloth.ui.common.UiState

interface NotificationRepository {

    suspend fun registerFCMToken(
        notificationRegisterRequest: NotificationRegisterRequest
    ): UiState<String>

    suspend fun updateNotificationStatus(notificationUpdateRequest: NotificationUpdateRequest): UiState<String>

    suspend fun fetchFCMToken(deviceId: String): UiState<NotificationFetchResponse>
}