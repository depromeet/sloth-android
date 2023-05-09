package com.depromeet.presentation.model


sealed class NotificationItem {
    object RestartOnBoarding : NotificationItem()
    data class Notification(
        val alarmContentId: Long,
        val alarmId: Long,
        val alarmType: String,
        val message: String,
        val occurrenceTime: String,
        val readTime: String?,
        val title: String,
        val url: String?
    ) : NotificationItem()
}



