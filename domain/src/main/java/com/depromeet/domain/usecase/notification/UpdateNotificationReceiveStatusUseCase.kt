package com.depromeet.domain.usecase.notification

import com.depromeet.domain.entity.NotificationUpdateRequestEntity
import com.depromeet.domain.repository.NotificationRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class UpdateNotificationReceiveStatusUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(notificationUpdateRequest: NotificationUpdateRequestEntity): Flow<Result<String>> {
        return notificationRepository.updateNotificationReceiveStatus(notificationUpdateRequest)
    }

//    operator fun invoke(notificationUpdateRequest: NotificationUpdateRequestEntity): Flow<Result<NotificationUpdateEntity>> {
//        return notificationRepository.updateNotificationStatus(notificationUpdateRequest)
//    }
}