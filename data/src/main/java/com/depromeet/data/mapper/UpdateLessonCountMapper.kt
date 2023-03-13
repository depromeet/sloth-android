package com.depromeet.data.mapper

import com.depromeet.data.model.response.lesson.UpdateLessonCountResponse
import com.depromeet.domain.entity.UpdateLessonCountEntity

internal fun UpdateLessonCountResponse.toEntity() = UpdateLessonCountEntity(
    isFinished = isFinished,
    lessonId = lessonId,
    presentNumber = presentNumber
)

internal fun UpdateLessonCountEntity.toModel() = UpdateLessonCountResponse(
    isFinished = isFinished,
    lessonId = lessonId,
    presentNumber = presentNumber
)