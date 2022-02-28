package com.depromeet.sloth.data.network.notification

data class NotificationUseRequest(
    val fcmToken: String,
    val isUse: Boolean
)
