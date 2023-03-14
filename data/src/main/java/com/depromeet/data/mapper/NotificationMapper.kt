package com.depromeet.data.mapper

import com.depromeet.data.model.response.notification.NotificationResponse
import com.depromeet.domain.entity.NotificationEntity


internal fun List<NotificationResponse>.toEntity(): List<NotificationEntity> = map {
    NotificationEntity(
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