package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.NotificationEntity
import com.depromeet.presentation.model.Notification


internal fun List<NotificationEntity>.toUiModel(): List<Notification> = map{
    Notification(
        alarmContentId = it.alarmContentId,
        alarmId = it.alarmId,
        alarmType = it.alarmType,
        message = it.message,
        occurrenceTime = it.occurrenceTime,
        readTime = it.readTime,
        title = it.title,
        url = it.url
    )
}