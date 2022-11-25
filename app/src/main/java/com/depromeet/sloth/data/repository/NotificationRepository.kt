package com.depromeet.sloth.data.repository

import com.depromeet.sloth.common.Result
import com.depromeet.sloth.data.model.request.notification.NotificationRegisterRequest
import com.depromeet.sloth.data.model.request.notification.NotificationUpdateRequest
import com.depromeet.sloth.data.model.response.notification.NotificationFetchResponse
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    fun registerFCMToken(notificationRegisterRequest: NotificationRegisterRequest): Flow<Result<String>>

    fun updateNotificationStatus(notificationUpdateRequest: NotificationUpdateRequest): Flow<Result<String>>

    fun fetchFCMToken(deviceId: String): Flow<Result<NotificationFetchResponse>>
}