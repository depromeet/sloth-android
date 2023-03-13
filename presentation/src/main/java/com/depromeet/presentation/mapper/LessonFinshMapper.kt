package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LessonFinishEntity
import com.depromeet.presentation.model.LessonFinish

internal fun LessonFinish.toEntity() = LessonFinishEntity(
    isFinished = isFinished
)

internal fun LessonFinishEntity.toUiModel() = LessonFinish(
    isFinished = isFinished
)