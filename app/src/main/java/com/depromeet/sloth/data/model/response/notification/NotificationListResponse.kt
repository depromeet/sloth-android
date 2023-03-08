package com.depromeet.sloth.data.model.response.notification

data class NotificationListResponse (
    val alarmContentId: Long,
    val alarmId: Long,
    val alarmType: String,
    val message: String,
    val occurrenceTime: String,
    val readTime: String?,
    val title: String,
    val url: String
) {
    companion object {
        val EMPTY = NotificationListResponse(0L, 0L, "", "", "", "", "", "")
    }
}