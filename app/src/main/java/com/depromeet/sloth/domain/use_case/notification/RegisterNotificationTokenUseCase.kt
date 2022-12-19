package com.depromeet.sloth.domain.use_case.notification

import com.depromeet.sloth.data.model.request.notification.NotificationRegisterRequest
import com.depromeet.sloth.domain.repository.NotificationRepository
import com.depromeet.sloth.util.Result
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject

class RegisterNotificationTokenUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(notificationRegisterRequest: NotificationRegisterRequest): Flow<Result<String>> {
        return notificationRepository.registerNotificationToken(notificationRegisterRequest)
    }
}