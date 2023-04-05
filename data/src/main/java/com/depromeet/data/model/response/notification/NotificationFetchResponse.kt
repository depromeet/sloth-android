package com.depromeet.data.model.response.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class NotificationFetchResponse(
    @SerialName("deviceId")
    val deviceId: String = "",
    @SerialName("fcmToken")
    val fcmToken: String? = ""
) {
    companion object {
        val EMPTY = NotificationFetchResponse("", "")
    }
}



