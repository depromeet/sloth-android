package com.depromeet.sloth.domain.usecase.notification

import com.depromeet.sloth.domain.repository.NotificationRepository
import com.depromeet.sloth.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateNotificationStateUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(alarmId: Long): Flow<Result<String>> {
        return notificationRepository.updateNotificationState(alarmId)
    }
}