package com.depromeet.data.mapper

import com.depromeet.data.model.response.lesson.LessonResponse
import com.depromeet.domain.entity.LessonEntity

internal fun List<LessonResponse>.toEntity(): List<LessonEntity> = map {
    LessonEntity(
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