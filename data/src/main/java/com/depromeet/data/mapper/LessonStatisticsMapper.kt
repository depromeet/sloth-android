package com.depromeet.data.mapper

import com.depromeet.data.model.response.lesson.LessonStatisticsResponse
import com.depromeet.domain.entity.LessonStatisticsEntity

internal fun LessonStatisticsResponse.toEntity() = LessonStatisticsEntity(
    expiredLessonsCnt = expiredLessonsCnt,
    expiredLessonsPrice = expiredLessonsPrice,
    finishedLessonsCnt = finishedLessonsCnt,
    finishedLessonsPrice = finishedLessonsPrice,
    notFinishedLessonsCnt = notFinishedLessonsCnt,
    notFinishedLessonsPrice = notFinishedLessonsPrice
)

internal fun LessonStatisticsEntity.toModel() = LessonStatisticsResponse(
    expiredLessonsCnt = expiredLessonsCnt,
    expiredLessonsPrice = expiredLessonsPrice,
    finishedLessonsCnt = finishedLessonsCnt,
    finishedLessonsPrice = finishedLessonsPrice,
    notFinishedLessonsCnt = notFinishedLessonsCnt,
    notFinishedLessonsPrice = notFinishedLessonsPrice
)