package com.depromeet.sloth.data.model.response.notification

import com.google.gson.annotations.SerializedName


/**
 * NotificationFetchResponse
 *
 * deviceId: "String"
 * fcmToken: "String"
 *
 */

data class NotificationFetchResponse(
    @SerializedName("deviceId")
    var deviceId: String = "",
    @SerializedName("fcmToken")
    var fcmToken: String? = ""
) {
    companion object {
        val EMPTY = NotificationFetchResponse("", "")
    }
}


