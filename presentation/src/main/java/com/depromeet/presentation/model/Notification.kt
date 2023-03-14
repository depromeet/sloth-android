package com.depromeet.presentation.model


data class Notification (
    val alarmContentId: Long,
    val alarmId: Long,
    val alarmType: String,
    val message: String,
    val occurrenceTime: String,
    val readTime: String?,
    val title: String,
    val url: String?
)


