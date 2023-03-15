package com.depromeet.data.mapper

import com.depromeet.data.model.response.lesson.LessonDeleteResponse
import com.depromeet.domain.entity.LessonDeleteEntity

internal fun LessonDeleteResponse.toEntity() = LessonDeleteEntity(
    code = code,
    message = message
)