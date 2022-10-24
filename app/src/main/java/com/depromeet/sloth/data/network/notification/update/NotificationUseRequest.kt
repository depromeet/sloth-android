package com.depromeet.sloth.data.network.notification.update

data class NotificationUseRequest(
    val fcmToken: String,
    val isUse: Boolean
)
