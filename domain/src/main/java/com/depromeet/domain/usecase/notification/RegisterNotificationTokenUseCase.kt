package com.depromeet.domain.usecase.notification

import com.depromeet.domain.repository.NotificationRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class RegisterNotificationTokenUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(fcmToken: String): Flow<Result<String>> {
        return notificationRepository.registerNotificationToken(fcmToken)
    }
}