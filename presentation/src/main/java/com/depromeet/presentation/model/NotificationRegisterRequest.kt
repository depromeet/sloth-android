package com.depromeet.presentation.model


data class NotificationRegisterRequest (
    val deviceId: String,
    val fcmToken: String
)


