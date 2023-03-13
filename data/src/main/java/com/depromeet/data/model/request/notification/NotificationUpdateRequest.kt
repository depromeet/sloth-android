package com.depromeet.data.model.request.notification

import com.google.gson.annotations.SerializedName


data class NotificationUpdateRequest(
    @SerializedName("isUse")
    val isUse: Boolean
)

