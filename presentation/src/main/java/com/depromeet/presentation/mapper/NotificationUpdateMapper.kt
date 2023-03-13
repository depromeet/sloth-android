package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.NotificationUpdateEntity
import com.depromeet.presentation.model.NotificationUpdate


internal fun NotificationUpdate.toEntity() = NotificationUpdateEntity(
    isUse = isUse
)

internal fun NotificationUpdateEntity.toUiModel() = NotificationUpdate(
    isUse = isUse
)