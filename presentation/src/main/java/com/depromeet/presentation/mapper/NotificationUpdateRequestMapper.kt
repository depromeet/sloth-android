package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.NotificationUpdateRequestEntity
import com.depromeet.presentation.model.NotificationUpdateRequest


internal fun NotificationUpdateRequest.toEntity() = NotificationUpdateRequestEntity(
    isUse = isUse
)