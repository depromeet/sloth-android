package com.depromeet.data.mapper

import com.depromeet.data.model.response.lesson.TodayLessonResponse
import com.depromeet.domain.entity.TodayLessonEntity


internal fun List<TodayLessonResponse>.toEntity(): List<TodayLessonEntity> = map {
    TodayLessonEntity(
        lessonId = it.lessonId,
        lessonName = it.lessonName,
        untilTodayFinished = it.untilTodayFinished,
        remainDay = it.remainDay,
        siteName = it.siteName,
        categoryName = it.categoryName,
        presentNumber = it.presentNumber,
        untilTodayNumber = it.untilTodayNumber,
        totalNumber = it.totalNumber
    )
}