package com.depromeet.data.model.response.notification

import com.google.gson.annotations.SerializedName


data class NotificationResponse (
    @SerializedName("alarmContentId")
    val alarmContentId: Long,
    @SerializedName("alarmId")
    val alarmId: Long,
    @SerializedName("alarmType")
    val alarmType: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("occurrenceTime")
    val occurrenceTime: String,
    @SerializedName("readTime")
    val readTime: String?,
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String
) {
    companion object {
        val EMPTY = NotificationResponse(0L, 0L, "", "", "", "", "", "")
    }
}

