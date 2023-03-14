package com.depromeet.domain.entity


data class NotificationEntity (
    val alarmContentId: Long,
    val alarmId: Long,
    val alarmType: String,
    val message: String,
    val occurrenceTime: String,
    val readTime: String?,
    val title: String,
    val url: String
)