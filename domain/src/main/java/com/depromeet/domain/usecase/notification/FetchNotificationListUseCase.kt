package com.depromeet.domain.usecase.notification

import com.depromeet.domain.entity.NotificationListEntity
import com.depromeet.domain.repository.NotificationRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FetchNotificationListUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {

    operator fun invoke(page: Int, size: Int): Flow<Result<List<NotificationListEntity>>> {
        return notificationRepository.fetchNotificationList(page, size)
    }

    /*
    operator fun invoke(page: Int, size: Int): Flow<PagingData<NotificationListResponse>> {
        return notificationRepository.fetchNotificationList(page, size)
    }
     */
}