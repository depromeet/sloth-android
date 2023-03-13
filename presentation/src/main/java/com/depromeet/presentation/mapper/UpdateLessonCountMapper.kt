package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.UpdateLessonCountEntity
import com.depromeet.presentation.model.UpdateLessonCount

internal fun UpdateLessonCount.toEntity() = UpdateLessonCountEntity(
    isFinished = isFinished,
    lessonId = lessonId,
    presentNumber = presentNumber
)

internal fun UpdateLessonCountEntity.toUiModel() = UpdateLessonCount(
    isFinished = isFinished,
    lessonId = lessonId,
    presentNumber = presentNumber
)