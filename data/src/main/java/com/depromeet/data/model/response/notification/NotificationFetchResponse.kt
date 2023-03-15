package com.depromeet.data.model.response.notification

import com.google.gson.annotations.SerializedName


data class NotificationFetchResponse(
    @SerializedName("deviceId")
    val deviceId: String = "",
    @SerializedName("fcmToken")
    val fcmToken: String? = ""
) {
    companion object {
        val EMPTY = NotificationFetchResponse("", "")
    }
}



