package com.depromeet.sloth.data.repository

import com.depromeet.sloth.common.Result
import com.depromeet.sloth.data.model.request.notification.NotificationRegisterRequest
import com.depromeet.sloth.data.model.request.notification.NotificationUpdateRequest
import com.depromeet.sloth.data.model.response.notification.NotificationFetchResponse
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    suspend fun registerFCMToken(
        notificationRegisterRequest: NotificationRegisterRequest
    ): Result<String>

    fun updateNotificationStatus(notificationUpdateRequest: NotificationUpdateRequest): Flow<Result<String>>

    // response 양식 바뀌면 해당 함수 사용
    // fun updateNotificationStatus(notificationUpdateRequest: NotificationUpdateRequest): Flow<Result<NotificationUpdateResponse>>

    suspend fun fetchFCMToken(deviceId: String): Result<NotificationFetchResponse>
}