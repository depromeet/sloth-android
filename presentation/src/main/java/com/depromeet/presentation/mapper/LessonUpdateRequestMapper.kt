package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LessonUpdateRequestEntity
import com.depromeet.presentation.model.LessonUpdateRequest


internal fun LessonUpdateRequest.toEntity() = LessonUpdateRequestEntity(
    categoryId = categoryId,
    lessonName = lessonName,
    price = price,
    siteId = siteId,
    totalNumber = totalNumber
)