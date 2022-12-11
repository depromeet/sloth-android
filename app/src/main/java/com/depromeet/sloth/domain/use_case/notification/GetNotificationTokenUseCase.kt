package com.depromeet.sloth.domain.use_case.notification

import com.depromeet.sloth.data.model.response.notification.NotificationFetchResponse
import com.depromeet.sloth.domain.repository.NotificationRepository
import com.depromeet.sloth.common.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationTokenUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(deviceId: String): Flow<Result<NotificationFetchResponse>> {
        return notificationRepository.fetchNotificationToken(deviceId)
    }
}