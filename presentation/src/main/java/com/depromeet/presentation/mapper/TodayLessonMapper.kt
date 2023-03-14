package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.TodayLessonEntity
import com.depromeet.presentation.model.TodayLesson


internal fun List<TodayLessonEntity>.toUiModel(): List<TodayLesson> = map {
    TodayLesson(
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