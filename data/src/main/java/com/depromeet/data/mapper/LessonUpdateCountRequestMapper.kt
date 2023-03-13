package com.depromeet.data.mapper

import com.depromeet.data.model.request.lesson.LessonUpdateCountRequest
import com.depromeet.domain.entity.LessonUpdateCountRequestEntity


internal fun LessonUpdateCountRequest.toEntity() = LessonUpdateCountRequestEntity(
    count = count,
    lessonId = lessonId
)

internal fun LessonUpdateCountRequestEntity.toModel() = LessonUpdateCountRequest(
    count = count,
    lessonId = lessonId
)