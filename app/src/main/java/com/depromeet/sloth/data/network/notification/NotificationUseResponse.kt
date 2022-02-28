package com.depromeet.sloth.data.network.notification

/**
 *  NotificationUseResponse
 *
 *  "fcmToken": "string",
 *  "isUse": 0,
 */

data class NotificationUseResponse (
    var fcmToken: String = "",
    var isUse: Boolean = false,
)