package com.depromeet.data.mapper

import com.depromeet.data.model.request.lesson.LessonUpdateRequest
import com.depromeet.domain.entity.LessonUpdateRequestEntity


internal fun LessonUpdateRequest.toEntity() = LessonUpdateRequestEntity(
    categoryId = categoryId,
    lessonName = lessonName,
    price = price,
    siteId = siteId,
    totalNumber = totalNumber
)

internal fun LessonUpdateRequestEntity.toModel() = LessonUpdateRequest(
    categoryId = categoryId,
    lessonName = lessonName,
    price = price,
    siteId = siteId,
    totalNumber = totalNumber
)