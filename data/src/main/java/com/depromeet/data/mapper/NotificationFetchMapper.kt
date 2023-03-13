package com.depromeet.data.mapper

import com.depromeet.data.model.response.notification.NotificationFetchResponse
import com.depromeet.domain.entity.NotificationFetchEntity


internal fun NotificationFetchResponse.toEntity() = NotificationFetchEntity(
    deviceId = deviceId,
    fcmToken = fcmToken
)

internal fun NotificationFetchEntity.toModel() = NotificationFetchResponse(
    deviceId = deviceId,
    fcmToken = fcmToken
)
