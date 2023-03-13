package com.depromeet.data.model.response.lesson

import com.google.gson.annotations.SerializedName


data class LessonStatisticsResponse (
    @SerializedName("expiredLessonsCnt")
    val expiredLessonsCnt: Int = 0,
    @SerializedName("expiredLessonsPrice")
    val expiredLessonsPrice: Int = 0,
    @SerializedName("finishedLessonsCnt")
    val finishedLessonsCnt: Int = 0,
    @SerializedName("finishedLessonsPrice")
    val finishedLessonsPrice: Int = 0,
    @SerializedName("notFinishedLessonsCnt")
    val notFinishedLessonsCnt: Int = 0,
    @SerializedName("notFinishedLessonsPrice")
    val notFinishedLessonsPrice: Int = 0,
) {
    companion object {
        val EMPTY = LessonStatisticsResponse(0, 0, 0, 0, 0, 0)
    }
}

