package com.depromeet.domain.entity


data class NotificationRegisterRequestEntity (
    val deviceId: String,
    val fcmToken: String
)
