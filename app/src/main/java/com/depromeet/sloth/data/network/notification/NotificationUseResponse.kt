package com.depromeet.sloth.data.network.notification

/**
 *  NotificationUseResponse
 *
 *  "fcmToken": "string",
 *  "isUse": false,
 */

data class NotificationUseResponse (
    var fcmToken: String = "",
    var isUse: Boolean = false,
)