package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LessonDetailEntity
import com.depromeet.presentation.model.LessonDetail

internal fun LessonDetail.toEntity() = LessonDetailEntity(
    alertDays = alertDays,
    categoryName = categoryName,
    currentProgressRate = currentProgressRate,
    endDate = endDate,
    goalProgressRate = goalProgressRate,
    isFinished = isFinished,
    lessonId = lessonId.toInt(),
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

internal fun LessonDetailEntity.toUiModel() = LessonDetail(
    alertDays = alertDays,
    categoryName = categoryName,
    currentProgressRate = currentProgressRate,
    endDate = endDate,
    goalProgressRate = goalProgressRate,
    isFinished = isFinished,
    lessonId = lessonId.toString(),
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