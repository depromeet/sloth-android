package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LessonEntity
import com.depromeet.presentation.model.Lesson


internal fun Lesson.toEntity() = LessonEntity(
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

internal fun LessonEntity.toUiModel() = Lesson(
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