package com.depromeet.data.model.response.lesson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class TodayLessonResponse (
    @SerialName("lessonId")
    val lessonId: Int,
    @SerialName("lessonName")
    val lessonName: String,
    @SerialName("untilTodayFinished")
    val untilTodayFinished: Boolean,
    @SerialName("remainDay")
    val remainDay: Int,
    @SerialName("siteName")
    val siteName: String,
    @SerialName("categoryName")
    val categoryName: String,
    @SerialName("presentNumber")
    var presentNumber: Int,
    @SerialName("untilTodayNumber")
    val untilTodayNumber: Int,
    @SerialName("totalNumber")
    val totalNumber: Int,
) {
    companion object {
        val EMPTY = TodayLessonResponse(0, "", false, 0, "", "", 0, 0, 0)
    }
}


