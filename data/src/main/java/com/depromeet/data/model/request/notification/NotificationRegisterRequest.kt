package com.depromeet.data.model.request.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class NotificationRegisterRequest (
    @SerialName("deviceId")
    val deviceId: String,
    @SerialName("fcmToken")
    val fcmToken: String
)

