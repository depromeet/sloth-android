package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LessonEntity
import com.depromeet.presentation.model.Lesson

internal fun Lesson.toEntity() = LessonEntity(
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

internal fun LessonEntity.toUiModel() = Lesson(
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