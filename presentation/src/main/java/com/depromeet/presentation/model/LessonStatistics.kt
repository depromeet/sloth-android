package com.depromeet.presentation.model


data class LessonStatistics (
    val expiredLessonsCnt: Int = 0,
    val expiredLessonsPrice: Int = 0,
    val finishedLessonsCnt: Int = 0,
    val finishedLessonsPrice: Int = 0,
    val notFinishedLessonsCnt: Int = 0,
    val notFinishedLessonsPrice: Int = 0,
)

