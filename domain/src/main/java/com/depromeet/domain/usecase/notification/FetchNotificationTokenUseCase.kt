package com.depromeet.domain.usecase.notification

import com.depromeet.domain.entity.NotificationFetchEntity
import com.depromeet.domain.repository.NotificationRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FetchNotificationTokenUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(deviceId: String): Flow<Result<NotificationFetchEntity>> {
        return notificationRepository.fetchNotificationToken(deviceId)
    }
}