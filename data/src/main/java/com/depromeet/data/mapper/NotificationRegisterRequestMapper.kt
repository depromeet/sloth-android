package com.depromeet.data.mapper

import com.depromeet.data.model.request.notification.NotificationRegisterRequest
import com.depromeet.domain.entity.NotificationRegisterRequestEntity


internal fun NotificationRegisterRequest.toEntity() = NotificationRegisterRequestEntity(
    deviceId = deviceId,
    fcmToken = fcmToken
)

internal fun NotificationRegisterRequestEntity.toModel() = NotificationRegisterRequest(
    deviceId = deviceId,
    fcmToken = fcmToken
)