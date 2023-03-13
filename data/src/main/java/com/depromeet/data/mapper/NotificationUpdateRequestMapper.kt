package com.depromeet.data.mapper

import com.depromeet.data.model.request.notification.NotificationUpdateRequest
import com.depromeet.domain.entity.NotificationUpdateRequestEntity


internal fun NotificationUpdateRequest.toEntity() = NotificationUpdateRequestEntity(
    isUse = isUse
)

internal fun NotificationUpdateRequestEntity.toModel() = NotificationUpdateRequest(
    isUse = isUse
)
