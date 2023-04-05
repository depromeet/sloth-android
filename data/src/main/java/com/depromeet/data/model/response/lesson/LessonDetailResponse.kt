package com.depromeet.data.model.response.lesson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LessonDetailResponse (
    @SerialName("alertDays")
    val alertDays: String? = "",
    @SerialName("categoryName")
    val categoryName: String = "",
    @SerialName("currentProgressRate")
    val currentProgressRate: Int = 0,
    @SerialName("endDate")
    val endDate: ArrayList<Int> = ArrayList(),
    @SerialName("goalProgressRate")
    val goalProgressRate: Int = 0,
    @SerialName("isFinished")
    val isFinished: Boolean = false,
    @SerialName("lessonId")
    val lessonId: Int = 0,
    @SerialName("lessonName")
    val lessonName: String = "",
    @SerialName("message")
    val message: String = "",
    @SerialName("presentNumber")
    val presentNumber: Int = 0,
    @SerialName("price")
    val price: Int = 0,
    @SerialName("remainDay")
    val remainDay: Int = 0,
    @SerialName("siteName")
    val siteName: String = "",
    @SerialName("startDate")
    val startDate: ArrayList<Int> = ArrayList(),
    @SerialName("totalNumber")
    val totalNumber: Int = 0,
    @SerialName("wastePrice")
    val wastePrice: Int = 0,
) {
    companion object {
        val EMPTY = LessonDetailResponse("", "", 0, ArrayList(),  0,false, 0, "", "", 0,0,0,"",ArrayList(),0,0)
    }
}