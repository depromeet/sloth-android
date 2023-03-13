package com.depromeet.data.mapper

import com.depromeet.data.model.response.lesson.LessonListResponse
import com.depromeet.domain.entity.LessonListEntity

internal fun LessonListResponse.toEntity() = LessonListEntity(
    categoryName = categoryName,
    currentProgressRate = currentProgressRate,
    endDate = endDate,
    goalProgressRate = goalProgressRate,
    isFinished = isFinished,
    lessonId = lessonId,
    lessonName = lessonName,
    lessonStatus = lessonStatus,
    price = price,
    siteName = siteName,
    startDate = startDate
)

internal fun LessonListEntity.toModel() = LessonListResponse(
    categoryName = categoryName,
    currentProgressRate = currentProgressRate,
    endDate = endDate,
    goalProgressRate = goalProgressRate,
    isFinished = isFinished,
    lessonId = lessonId,
    lessonName = lessonName,
    lessonStatus = lessonStatus,
    price = price,
    siteName = siteName,
    startDate = startDate
)