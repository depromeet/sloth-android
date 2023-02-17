package com.depromeet.sloth.data.model.response.lesson

import com.google.gson.annotations.SerializedName


data class TodayLessonResponse (
    @SerializedName("categoryName")
    val categoryName: String,
    @SerializedName("lessonId")
    val lessonId: Int,
    @SerializedName("lessonName")
    val lessonName: String,
    @SerializedName("presentNumber")
    var presentNumber: Int,
    @SerializedName("remainDay")
    val remainDay: Int,
    @SerializedName("siteName")
    val siteName: String,
    @SerializedName("untilTodayFinished")
    val untilTodayFinished: Boolean,
    @SerializedName("untilTodayNumber")
    val untilTodayNumber: Int,
    @SerializedName("totalNumber")
    val totalNumber: Int
) {
    companion object {
        val EMPTY = TodayLessonResponse("", 0, "", 0, 0, "", false, 0, 0)
    }
}