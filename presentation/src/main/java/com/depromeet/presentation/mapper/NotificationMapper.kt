package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.NotificationEntity
import com.depromeet.presentation.model.NotificationItem


internal fun List<NotificationEntity>.toUiModel(): List<NotificationItem.Notification> = map {
    NotificationItem.Notification(
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