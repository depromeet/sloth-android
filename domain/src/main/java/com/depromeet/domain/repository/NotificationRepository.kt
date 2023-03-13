package com.depromeet.domain.repository

import com.depromeet.domain.entity.NotificationFetchEntity
import com.depromeet.domain.entity.NotificationListEntity
import com.depromeet.domain.entity.NotificationUpdateRequestEntity
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow


interface NotificationRepository {

    fun registerNotificationToken(fcmToken: String): Flow<Result<String>>

    fun updateNotificationStatus(notificationUpdateRequestEntity: NotificationUpdateRequestEntity): Flow<Result<String>>

    fun fetchNotificationToken(deviceId: String): Flow<Result<NotificationFetchEntity>>

    fun fetchNotificationList(page: Int, size: Int): Flow<Result<List<NotificationListEntity>>>

    // fun fetchNotificationList(page: Int, size: Int): Flow<PagingData<NotificationListEntity>>

    fun updateNotificationState(alarmId: Long): Flow<Result<String>>
}