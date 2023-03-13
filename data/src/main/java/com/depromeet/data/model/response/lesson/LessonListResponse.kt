package com.depromeet.data.model.response.lesson

import com.google.gson.annotations.SerializedName


data class LessonListResponse (
    @SerializedName("categoryName")
    val categoryName: String = "",
    @SerializedName("currentProgressRate")
    val currentProgressRate: Int = 0,
    @SerializedName("endDate")
    val endDate: String = "",
    @SerializedName("goalProgressRate")
    val goalProgressRate: Int = 0,
    @SerializedName("isFinished")
    val isFinished: Boolean = false,
    @SerializedName("lessonId")
    val lessonId: Int = 0,
    @SerializedName("lessonName")
    val lessonName: String = "",
    @SerializedName("lessonStatus")
    val lessonStatus: String = "",
    @SerializedName("price")
    val price: Int = 0,
    @SerializedName("remainDay")
    val remainDay: Int = 0,
    @SerializedName("siteName")
    val siteName: String = "",
    @SerializedName("startDate")
    val startDate: String = "",
    @SerializedName("totalNumber")
    val totalNumber: Int = 0
) {
    companion object {
        val EMPTY = LessonListResponse("", 0, "", 0, false, 0, "", "", 0, 0, "", "", 0)
    }
}

