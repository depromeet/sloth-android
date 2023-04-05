package com.depromeet.data.model.response.lesson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LessonStatisticsResponse (
    @SerialName("expiredLessonsCnt")
    val expiredLessonsCnt: Int = 0,
    @SerialName("expiredLessonsPrice")
    val expiredLessonsPrice: Int = 0,
    @SerialName("finishedLessonsCnt")
    val finishedLessonsCnt: Int = 0,
    @SerialName("finishedLessonsPrice")
    val finishedLessonsPrice: Int = 0,
    @SerialName("notFinishedLessonsCnt")
    val notFinishedLessonsCnt: Int = 0,
    @SerialName("notFinishedLessonsPrice")
    val notFinishedLessonsPrice: Int = 0,
) {
    companion object {
        val EMPTY = LessonStatisticsResponse(0, 0, 0, 0, 0, 0)
    }
}

