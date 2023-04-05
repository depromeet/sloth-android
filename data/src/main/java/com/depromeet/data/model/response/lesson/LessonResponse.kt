package com.depromeet.data.model.response.lesson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LessonResponse (
    @SerialName("categoryName")
    val categoryName: String = "",
    @SerialName("currentProgressRate")
    val currentProgressRate: Int = 0,
    @SerialName("endDate")
    val endDate: String = "",
    @SerialName("goalProgressRate")
    val goalProgressRate: Int = 0,
    @SerialName("isFinished")
    val isFinished: Boolean = false,
    @SerialName("lessonId")
    val lessonId: Int = 0,
    @SerialName("lessonName")
    val lessonName: String = "",
    @SerialName("lessonStatus")
    val lessonStatus: String = "",
    @SerialName("price")
    val price: Int = 0,
    @SerialName("remainDay")
    val remainDay: Int = 0,
    @SerialName("siteName")
    val siteName: String = "",
    @SerialName("startDate")
    val startDate: String = "",
    @SerialName("totalNumber")
    val totalNumber: Int = 0
) {
    companion object {
        val EMPTY = LessonResponse("", 0, "", 0, false, 0, "", "", 0, 0, "", "", 0)
    }
}

