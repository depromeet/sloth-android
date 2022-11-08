package com.depromeet.sloth.data.model.response.lesson

import com.google.gson.annotations.SerializedName


data class LessonAllResponse (
    @SerializedName("categoryName")
    var categoryName: String = "",
    @SerializedName("currentProgressRate")
    var currentProgressRate: Int = 0,
    @SerializedName("endDate")
    var endDate: String = "",
    @SerializedName("goalProgressRate")
    var goalProgressRate: Int = 0,
    @SerializedName("isFinished")
    var isFinished: Boolean = false,
    @SerializedName("lessonId")
    var lessonId: Int = 0,
    @SerializedName("lessonName")
    var lessonName: String = "",
    @SerializedName("lessonStatus")
    var lessonStatus: String = "",
    @SerializedName("price")
    var price: Int = 0,
    @SerializedName("remainDay")
    var remainDay: Int = 0,
    @SerializedName("siteName")
    var siteName: String = "",
    @SerializedName("startDate")
    var startDate: String = "",
    @SerializedName("totalNumber")
    var totalNumber: Int = 0
) {
    companion object {
        val EMPTY = LessonAllResponse("", 0, "", 0, false, 0, "", "", 0, 0, "", "", 0)
    }
}