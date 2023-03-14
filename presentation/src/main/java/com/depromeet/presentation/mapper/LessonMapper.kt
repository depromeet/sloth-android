package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LessonEntity
import com.depromeet.presentation.model.Lesson


internal fun List<LessonEntity>.toUiModel(): List<Lesson> = map {
    Lesson(
        categoryName = it.categoryName,
        currentProgressRate = it.currentProgressRate,
        endDate = it.endDate,
        goalProgressRate = it.goalProgressRate,
        isFinished = it.isFinished,
        lessonId = it.lessonId,
        lessonName = it.lessonName,
        lessonStatus = it.lessonStatus,
        price = it.price,
        remainDay = it.remainDay,
        siteName = it.siteName,
        startDate = it.startDate,
        totalNumber = it.totalNumber
    )
}