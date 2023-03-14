package com.depromeet.data.mapper

import com.depromeet.data.model.response.lesson.LessonListResponse
import com.depromeet.domain.entity.LessonEntity

internal fun LessonListResponse.toEntity() = LessonEntity(
    categoryName = categoryName,
    currentProgressRate = currentProgressRate,
    endDate = endDate,
    goalProgressRate = goalProgressRate,
    isFinished = isFinished,
    lessonId = lessonId,
    lessonName = lessonName,
    lessonStatus = lessonStatus,
    price = price,
    remainDay = remainDay,
    siteName = siteName,
    startDate = startDate,
    totalNumber = totalNumber
)

internal fun LessonEntity.toModel() = LessonListResponse(
    categoryName = categoryName,
    currentProgressRate = currentProgressRate,
    endDate = endDate,
    goalProgressRate = goalProgressRate,
    isFinished = isFinished,
    lessonId = lessonId,
    lessonName = lessonName,
    lessonStatus = lessonStatus,
    price = price,
    remainDay = remainDay,
    siteName = siteName,
    startDate = startDate,
    totalNumber = totalNumber
)