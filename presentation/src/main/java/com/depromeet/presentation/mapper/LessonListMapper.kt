package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LessonListEntity
import com.depromeet.presentation.model.LessonList

internal fun LessonList.toEntity() = LessonListEntity(
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

internal fun LessonListEntity.toUiModel() = LessonList(
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