package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LessonRegisterEntity
import com.depromeet.presentation.model.LessonRegister


internal fun LessonRegister.toEntity() = LessonRegisterEntity(
    lessonId = lessonId
)

internal fun LessonRegisterEntity.toUiModel() = LessonRegister(
    lessonId = lessonId
)