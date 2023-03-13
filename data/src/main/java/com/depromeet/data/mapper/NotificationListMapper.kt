package com.depromeet.data.mapper

import com.depromeet.data.model.response.notification.NotificationListResponse
import com.depromeet.domain.entity.NotificationListEntity


internal fun NotificationListResponse.toEntity() = NotificationListEntity(
    alarmContentId = alarmContentId,
    alarmId = alarmId,
    alarmType = alarmType,
    message = message,
    occurrenceTime = occurrenceTime,
    readTime = readTime,
    title = title,
    url = url
)

internal fun NotificationListEntity.toModel() = NotificationListResponse(
    alarmContentId = alarmContentId,
    alarmId = alarmId,
    alarmType = alarmType,
    message = message,
    occurrenceTime = occurrenceTime,
    readTime = readTime,
    title = title,
    url = url
)