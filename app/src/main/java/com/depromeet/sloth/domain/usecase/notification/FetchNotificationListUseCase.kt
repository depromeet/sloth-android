package com.depromeet.sloth.domain.usecase.notification

import com.depromeet.sloth.data.model.response.notification.NotificationListResponse
import com.depromeet.sloth.domain.repository.NotificationRepository
import com.depromeet.sloth.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchNotificationListUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {

    operator fun invoke(page: Int, size: Int): Flow<Result<List<NotificationListResponse>>> {
        return notificationRepository.fetchNotificationList(page, size)
    }

    /*
    operator fun invoke(page: Int, size: Int): Flow<PagingData<NotificationListResponse>> {
        return notificationRepository.fetchNotificationList(page, size)
    }
     */
}