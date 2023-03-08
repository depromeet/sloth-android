package com.depromeet.sloth.data.model.response.lesson

data class LessonStatisticsResponse (
    val expiredLessonsCnt: Int = 0,
    val expiredLessonsPrice: Int = 0,
    val finishedLessonsCnt: Int = 0,
    val finishedLessonsPrice: Int = 0,
    val notFinishedLessonsCnt: Int = 0,
    val notFinishedLessonsPrice: Int = 0,
) {
    companion object {
        val EMPTY = LessonStatisticsResponse(0, 0, 0, 0, 0, 0)
    }
}