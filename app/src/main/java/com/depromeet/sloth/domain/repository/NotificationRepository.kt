package com.depromeet.sloth.domain.repository

import com.depromeet.sloth.data.model.request.notification.NotificationUpdateRequest
import com.depromeet.sloth.data.model.response.notification.NotificationFetchResponse
import com.depromeet.sloth.data.model.response.notification.NotificationListResponse
import com.depromeet.sloth.util.Result
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    fun registerNotificationToken(fcmToken: String): Flow<Result<String>>

    fun updateNotificationStatus(notificationUpdateRequest: NotificationUpdateRequest): Flow<Result<String>>

    fun fetchNotificationToken(deviceId: String): Flow<Result<NotificationFetchResponse>>

    fun fetchNotificationList(page: Int, size: Int): Flow<Result<List<NotificationListResponse>>>

    fun updateNotificationState(alarmId: Long): Flow<Result<String>>
}