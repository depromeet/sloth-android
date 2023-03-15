package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.NotificationFetchEntity
import com.depromeet.presentation.model.NotificationFetch


internal fun NotificationFetchEntity.toUiModel() = NotificationFetch(
    deviceId = deviceId,
    fcmToken = fcmToken
)
