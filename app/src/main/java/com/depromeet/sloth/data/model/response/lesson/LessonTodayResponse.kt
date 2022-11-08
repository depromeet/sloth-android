package com.depromeet.sloth.data.model.response.lesson

import com.google.gson.annotations.SerializedName


data class LessonTodayResponse (
    @SerializedName("categoryName")
    val categoryName: String,
    @SerializedName("lessonId")
    val lessonId: Int,
    @SerializedName("lessonName")
    var lessonName: String,
    @SerializedName("presentNumber")
    var presentNumber: Int,
    @SerializedName("remainDay")
    var remainDay: Int,
    @SerializedName("siteName")
    var siteName: String,
    @SerializedName("untilTodayFinished")
    var untilTodayFinished: Boolean,
    @SerializedName("untilTodayNumber")
    val untilTodayNumber: Int,
    @SerializedName("totalNumber")
    val totalNumber: Int
) {
    companion object {
        val EMPTY = LessonTodayResponse("", 0, "", 0, 0, "", false, 0, 0)
    }
}