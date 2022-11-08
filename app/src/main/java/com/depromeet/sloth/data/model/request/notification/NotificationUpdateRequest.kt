package com.depromeet.sloth.data.model.request.notification

import com.google.gson.annotations.SerializedName

data class NotificationUpdateRequest(
    @SerializedName("isUse")
    val isUse: Boolean
)
