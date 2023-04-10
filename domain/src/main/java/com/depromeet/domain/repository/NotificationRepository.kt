package com.depromeet.domain.repository

import com.depromeet.domain.entity.NotificationFetchEntity
import com.depromeet.domain.entity.NotificationEntity
import com.depromeet.domain.entity.NotificationUpdateRequestEntity
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow


interface NotificationRepository {

    fun registerNotificationToken(fcmToken: String): Flow<Result<String>>

    fun updateNotificationReceiveStatus(notificationUpdateRequestEntity: NotificationUpdateRequestEntity): Flow<Result<String>>

    fun fetchNotificationToken(deviceId: String): Flow<Result<NotificationFetchEntity>>

    fun fetchNotificationList(page: Int, size: Int): Flow<Result<List<NotificationEntity>>>

    // fun fetchNotificationList(page: Int, size: Int): Flow<PagingData<NotificationListEntity>>

    fun updateNotificationReadStatus(notificationId: Long): Flow<Result<String>>
}