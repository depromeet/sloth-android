package com.depromeet.data.model.response.notification

import com.google.gson.annotations.SerializedName

data class NotificationUpdateResponse(
    @SerializedName("isUse")
    val isUse: Boolean
) {
    companion object {
        val EMPTY = NotificationUpdateResponse(false)
    }
}


