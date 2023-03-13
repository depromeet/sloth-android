package com.depromeet.data.mapper

import com.depromeet.data.model.response.lesson.TodayLessonResponse
import com.depromeet.domain.entity.TodayLessonEntity


internal fun TodayLessonResponse.toEntity() = TodayLessonEntity(
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

internal fun TodayLessonEntity.toModel() = TodayLessonResponse(
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