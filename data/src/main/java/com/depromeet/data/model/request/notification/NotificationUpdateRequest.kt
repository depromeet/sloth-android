package com.depromeet.data.model.request.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class NotificationUpdateRequest(
    @SerialName("isUse")
    val isUse: Boolean
)

