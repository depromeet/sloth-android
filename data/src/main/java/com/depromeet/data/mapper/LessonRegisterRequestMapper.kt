package com.depromeet.data.mapper

import com.depromeet.data.model.request.lesson.LessonRegisterRequest
import com.depromeet.domain.entity.LessonRegisterRequestEntity


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

internal fun LessonRegisterRequestEntity.toModel() = LessonRegisterRequest(
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