package com.depromeet.data.mapper

import com.depromeet.data.model.response.lesson.LessonFinishResponse
import com.depromeet.domain.entity.LessonFinishEntity

internal fun LessonFinishResponse.toEntity() = LessonFinishEntity(
    isFinished = isFinished
)

internal fun LessonFinishEntity.toModel() = LessonFinishResponse(
    isFinished = isFinished
)