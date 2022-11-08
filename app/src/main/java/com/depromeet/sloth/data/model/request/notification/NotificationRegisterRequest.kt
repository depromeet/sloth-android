package com.depromeet.sloth.data.model.request.notification

import com.google.gson.annotations.SerializedName

data class NotificationRegisterRequest (
    @SerializedName("deviceId")
    val deviceId: String,
    @SerializedName("fcmToken")
    val fcmToken: String
)
