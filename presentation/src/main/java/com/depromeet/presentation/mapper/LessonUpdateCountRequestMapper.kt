package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LessonUpdateCountRequestEntity
import com.depromeet.presentation.model.LessonUpdateCountRequest


internal fun LessonUpdateCountRequest.toEntity() = LessonUpdateCountRequestEntity(
    count = count,
    lessonId = lessonId
)

internal fun LessonUpdateCountRequestEntity.toUiModel() = LessonUpdateCountRequest(
    count = count,
    lessonId = lessonId
)