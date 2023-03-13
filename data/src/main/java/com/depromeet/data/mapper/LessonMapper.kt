package com.depromeet.data.mapper

import com.depromeet.data.model.response.lesson.LessonResponse
import com.depromeet.domain.entity.LessonEntity


internal fun LessonResponse.toEntity() = LessonEntity(
    alertDays = alertDays,
    categoryName = categoryName,
    endDate = endDate,
    lessonName = lessonName,
    message = message,
    price = price,
    siteName = siteName,
    startDate = startDate,
    totalNumber = totalNumber
)

internal fun LessonEntity.toModel() = LessonResponse(
    alertDays = alertDays,
    categoryName = categoryName,
    endDate = endDate,
    lessonName = lessonName,
    message = message,
    price = price,
    siteName = siteName,
    startDate = startDate,
    totalNumber = totalNumber
)