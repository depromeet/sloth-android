package com.depromeet.sloth.data.network.notification

import com.depromeet.sloth.data.network.notification.fetch.NotificationFetchResponse
import com.depromeet.sloth.data.network.notification.register.NotificationRegisterRequest
import com.depromeet.sloth.data.network.notification.update.NotificationUseResponse
import com.depromeet.sloth.data.network.notification.update.NotificationUseRequest

interface NotificationRepository {

    suspend fun registerFCMToken(
        notificationRegisterRequest: NotificationRegisterRequest
    ): NotificationState<String>

    suspend fun updateFCMTokenUse(
        notificationUseRequest: NotificationUseRequest
    ): NotificationState<NotificationUseResponse>

    suspend fun fetchFCMToken(
        deviceId: String
    ): NotificationState<NotificationFetchResponse>
}