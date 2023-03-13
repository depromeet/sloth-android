package com.depromeet.data.mapper

import com.depromeet.data.model.response.lesson.LessonDetailResponse
import com.depromeet.domain.entity.LessonDetailEntity

internal fun LessonDetailResponse.toEntity() = LessonDetailEntity(
    alertDays = alertDays,
    categoryName = categoryName,
    currentProgressRate = currentProgressRate,
    endDate = endDate,
    goalProgressRate = goalProgressRate,
    isFinished = isFinished,
    lessonId = lessonId,
    lessonName = lessonName,
    message = message,
    presentNumber = presentNumber,
    price = price,
    remainDay = remainDay,
    siteName = siteName,
    startDate = startDate,
    totalNumber = totalNumber,
    wastePrice = wastePrice
)

internal fun LessonDetailEntity.toModel() = LessonDetailResponse(
    alertDays = alertDays,
    categoryName = categoryName,
    currentProgressRate = currentProgressRate,
    endDate = endDate,
    goalProgressRate = goalProgressRate,
    isFinished = isFinished,
    lessonId = lessonId,
    lessonName = lessonName,
    message = message,
    presentNumber = presentNumber,
    price = price,
    remainDay = remainDay,
    siteName = siteName,
    startDate = startDate,
    totalNumber = totalNumber,
    wastePrice = wastePrice
)