package com.depromeet.data.model.response.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class NotificationResponse (
    @SerialName("alarmContentId")
    val alarmContentId: Long,
    @SerialName("alarmId")
    val alarmId: Long,
    @SerialName("alarmType")
    val alarmType: String,
    @SerialName("message")
    val message: String,
    @SerialName("occurrenceTime")
    val occurrenceTime: String,
    @SerialName("readTime")
    val readTime: String?,
    @SerialName("title")
    val title: String,
    @SerialName("url")
    val url: String?
) {
    companion object {
        val EMPTY = NotificationResponse(0L, 0L, "", "", "", "", "", "")
    }
}

