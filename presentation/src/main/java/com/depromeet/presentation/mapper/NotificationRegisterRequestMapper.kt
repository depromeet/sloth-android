package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.NotificationRegisterRequestEntity
import com.depromeet.presentation.model.NotificationRegisterRequest


internal fun NotificationRegisterRequest.toEntity() = NotificationRegisterRequestEntity(
    deviceId = deviceId,
    fcmToken = fcmToken
)

internal fun NotificationRegisterRequestEntity.toUiModel() = NotificationRegisterRequest(
    deviceId = deviceId,
    fcmToken = fcmToken
)