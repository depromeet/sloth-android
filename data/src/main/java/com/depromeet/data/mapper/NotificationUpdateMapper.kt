package com.depromeet.data.mapper

import com.depromeet.data.model.response.notification.NotificationUpdateResponse
import com.depromeet.domain.entity.NotificationUpdateEntity


internal fun NotificationUpdateResponse.toEntity() = NotificationUpdateEntity(
    isUse = isUse
)

internal fun NotificationUpdateEntity.toModel() = NotificationUpdateResponse(
    isUse = isUse
)