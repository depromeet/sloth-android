package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LessonRegisterRequestEntity
import com.depromeet.presentation.model.LessonRegisterRequest


internal fun LessonRegisterRequest.toEntity() = LessonRegisterRequestEntity(
    alertDays = alertDays,
    categoryId = categoryId,
    endDate = endDate,
    lessonName = lessonName,
    message = message,
    price = price,
    siteId = siteId,
    startDate = startDate,
    totalNumber = totalNumber
)