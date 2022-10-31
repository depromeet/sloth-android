package com.depromeet.sloth.data.repository

import com.depromeet.sloth.data.network.notification.NotificationState
import com.depromeet.sloth.data.network.notification.fetch.NotificationFetchResponse
import com.depromeet.sloth.data.network.notification.register.NotificationRegisterRequest
import com.depromeet.sloth.data.network.notification.update.NotificationUpdateRequest

interface NotificationRepository {

    suspend fun registerFCMToken(
        notificationRegisterRequest: NotificationRegisterRequest
    ): NotificationState<String>

    suspend fun updateNotificationStatus(notificationUpdateRequest: NotificationUpdateRequest): NotificationState<String>

    suspend fun fetchFCMToken(deviceId: String): NotificationState<NotificationFetchResponse>
}