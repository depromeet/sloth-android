package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.NotificationListEntity
import com.depromeet.presentation.model.NotificationList


internal fun NotificationList.toEntity() = NotificationListEntity(
    alarmContentId = alarmContentId,
    alarmId = alarmId,
    alarmType = alarmType,
    message = message,
    occurrenceTime = occurrenceTime,
    readTime = readTime,
    title = title,
    url = url
)

internal fun NotificationListEntity.toUiModel() = NotificationList(
    alarmContentId = alarmContentId,
    alarmId = alarmId,
    alarmType = alarmType,
    message = message,
    occurrenceTime = occurrenceTime,
    readTime = readTime,
    title = title,
    url = url
)