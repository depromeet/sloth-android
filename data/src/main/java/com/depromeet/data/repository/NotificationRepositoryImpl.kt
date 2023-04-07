package com.depromeet.data.repository

import com.depromeet.data.source.remote.NotificationRemoteDataSource
import com.depromeet.domain.entity.NotificationEntity
import com.depromeet.domain.entity.NotificationFetchEntity
import com.depromeet.domain.entity.NotificationUpdateRequestEntity
import com.depromeet.domain.repository.NotificationRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class NotificationRepositoryImpl @Inject constructor(
    private val notificationRemoteDataSource: NotificationRemoteDataSource
) : NotificationRepository {

    override fun registerNotificationToken(fcmToken: String): Flow<Result<String>> {
        return notificationRemoteDataSource.registerNotificationToken(fcmToken)
    }

    override fun updateNotificationStatus(notificationUpdateRequestEntity: NotificationUpdateRequestEntity): Flow<Result<String>> {
        return notificationRemoteDataSource.updateNotificationStatus(notificationUpdateRequestEntity)
    }

    override fun fetchNotificationToken(deviceId: String): Flow<Result<NotificationFetchEntity>> {
        return notificationRemoteDataSource.fetchNotificationToken(deviceId)
    }

    override fun fetchNotificationList(page: Int, size: Int): Flow<Result<List<NotificationEntity>>> {
        return notificationRemoteDataSource.fetchNotificationList(page, size)
    }

//    override fun fetchNotificationList(page: Int, size: Int): Flow<PagingData<NotificationListEntity>> {
//        return notificationRemoteDataSource.fetchNotificationList(page, size)
//    }

    override fun updateNotificationState(notificationId: Long): Flow<Result<String>> {
        return notificationRemoteDataSource.updateNotificationState(notificationId)
    }
}