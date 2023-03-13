package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LessonDeleteEntity
import com.depromeet.presentation.model.LessonDelete

internal fun LessonDelete.toEntity() = LessonDeleteEntity(
    code = code,
    message = message
)

internal fun LessonDeleteEntity.toUiModel() = LessonDelete(
    code = code,
    message = message
)