package com.depromeet.data.mapper

import com.depromeet.data.model.response.lesson.LessonRegisterResponse
import com.depromeet.domain.entity.LessonRegisterEntity


internal fun LessonRegisterResponse.toEntity() = LessonRegisterEntity(
    lessonId = lessonId
)