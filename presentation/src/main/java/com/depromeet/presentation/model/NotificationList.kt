package com.depromeet.presentation.model

// TODO 이런 케이스의 data class 단수형으로 바꿔도 될듯
data class NotificationList (
    val alarmContentId: Long,
    val alarmId: Long,
    val alarmType: String,
    val message: String,
    val occurrenceTime: String,
    val readTime: String?,
    val title: String,
    val url: String
)


