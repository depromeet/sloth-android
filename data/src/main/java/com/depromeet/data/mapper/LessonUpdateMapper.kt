package com.depromeet.data.mapper

import com.depromeet.data.model.response.lesson.LessonUpdateResponse
import com.depromeet.domain.entity.LessonUpdateEntity

internal fun LessonUpdateResponse.toEntity() = LessonUpdateEntity(
    categoryId = categoryId,
    lessonId = lessonId,
    lessonName = lessonName,
    siteId = siteId,
    totalNumber = totalNumber
)