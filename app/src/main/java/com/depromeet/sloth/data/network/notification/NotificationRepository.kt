package com.depromeet.sloth.data.network.notification

interface NotificationRepository {

    suspend fun registerFCMToken(
        notificationRequest: NotificationRegisterRequest
    ): NotificationRegisterState<String>

    suspend fun updateFCMTokenUse(
        notificationUseRequest: NotificationUseRequest
    ): NotificationUseState<NotificationUseResponse>
}