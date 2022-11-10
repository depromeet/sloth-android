package com.depromeet.sloth.data.repository

import com.depromeet.sloth.data.model.response.notification.NotificationFetchResponse
import com.depromeet.sloth.data.model.request.notification.NotificationRegisterRequest
import com.depromeet.sloth.data.model.request.notification.NotificationUpdateRequest
import com.depromeet.sloth.common.Result

interface NotificationRepository {

    suspend fun registerFCMToken(
        notificationRegisterRequest: NotificationRegisterRequest
    ): Result<String>

    suspend fun updateNotificationStatus(notificationUpdateRequest: NotificationUpdateRequest): Result<String>

    suspend fun fetchFCMToken(deviceId: String): Result<NotificationFetchResponse>
}