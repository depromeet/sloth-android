package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.TodayLessonEntity
import com.depromeet.presentation.model.TodayLesson


internal fun TodayLesson.toEntity() = TodayLessonEntity(
    lessonId = lessonId,
    lessonName = lessonName,
    untilTodayFinished = untilTodayFinished,
    remainDay = remainDay,
    siteName = siteName,
    categoryName = categoryName,
    presentNumber = presentNumber,
    untilTodayNumber = untilTodayNumber,
    totalNumber = totalNumber
)

internal fun TodayLessonEntity.toUiModel() = TodayLesson(
    lessonId = lessonId,
    lessonName = lessonName,
    untilTodayFinished = untilTodayFinished,
    remainDay = remainDay,
    siteName = siteName,
    categoryName = categoryName,
    presentNumber = presentNumber,
    untilTodayNumber = untilTodayNumber,
    totalNumber = totalNumber
)