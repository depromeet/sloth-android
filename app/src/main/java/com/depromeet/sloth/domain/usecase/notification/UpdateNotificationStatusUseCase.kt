package com.depromeet.sloth.domain.usecase.notification

import com.depromeet.sloth.util.Result
import com.depromeet.sloth.data.model.request.notification.NotificationUpdateRequest
import com.depromeet.sloth.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateNotificationStatusUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(notificationUpdateRequest: NotificationUpdateRequest): Flow<Result<String>> {
        return notificationRepository.updateNotificationStatus(notificationUpdateRequest)
    }

//    operator fun invoke(notificationUpdateRequest: NotificationUpdateRequest): Flow<Result<NotificationUpdateResponse>> {
//        return notificationRepository.updateNotificationStatus(notificationUpdateRequest)
//    }
}