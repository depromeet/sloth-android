package com.depromeet.sloth.data.network.notification.register

data class NotificationRegisterRequest(
    val deviceId: String,
    val fcmToken: String
)
