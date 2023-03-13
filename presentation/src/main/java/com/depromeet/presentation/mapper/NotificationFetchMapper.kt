package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.NotificationFetchEntity
import com.depromeet.presentation.model.NotificationFetch


internal fun NotificationFetch.toEntity() = NotificationFetchEntity(
    deviceId = deviceId,
    fcmToken = fcmToken
)

internal fun NotificationFetchEntity.toUiModel() = NotificationFetch(
    deviceId = deviceId,
    fcmToken = fcmToken
)
