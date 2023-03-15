package com.depromeet.domain.entity


data class LessonStatisticsEntity (
    val expiredLessonsCnt: Int,
    val expiredLessonsPrice: Int,
    val finishedLessonsCnt: Int,
    val finishedLessonsPrice: Int,
    val notFinishedLessonsCnt: Int,
    val notFinishedLessonsPrice: Int,
)