package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LessonStatisticsEntity
import com.depromeet.presentation.model.LessonStatistics

internal fun LessonStatisticsEntity.toUiModel() = LessonStatistics(
    expiredLessonsCnt = expiredLessonsCnt,
    expiredLessonsPrice = expiredLessonsPrice,
    finishedLessonsCnt = finishedLessonsCnt,
    finishedLessonsPrice = finishedLessonsPrice,
    notFinishedLessonsCnt = notFinishedLessonsCnt,
    notFinishedLessonsPrice = notFinishedLessonsPrice
)