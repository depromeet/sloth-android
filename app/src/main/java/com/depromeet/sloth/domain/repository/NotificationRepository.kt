package com.depromeet.sloth.domain.repository

import com.depromeet.sloth.util.Result
import com.depromeet.sloth.data.model.request.notification.NotificationRegisterRequest
import com.depromeet.sloth.data.model.request.notification.NotificationUpdateRequest
import com.depromeet.sloth.data.model.response.notification.NotificationFetchResponse
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    fun registerNotificationToken(notificationRegisterRequest: NotificationRegisterRequest): Flow<Result<String>>

    fun updateNotificationStatus(notificationUpdateRequest: NotificationUpdateRequest): Flow<Result<String>>

    fun fetchNotificationToken(deviceId: String): Flow<Result<NotificationFetchResponse>>
}