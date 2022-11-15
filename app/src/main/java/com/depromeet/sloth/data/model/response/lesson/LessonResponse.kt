package com.depromeet.sloth.data.model.response.lesson

import com.google.gson.annotations.SerializedName

data class LessonResponse(
    @SerializedName("alertDays")
    val alertDays: String? = "",
    @SerializedName("categoryName")
    val categoryName: String = "",
    @SerializedName("endDate")
    val endDate: ArrayList<String> = arrayListOf(),
    @SerializedName("lessonName")
    val lessonName: String = "",
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("price")
    val price: Int = 0,
    @SerializedName("siteName")
    val siteName: String = "",
    @SerializedName("startDate")
    val startDate: ArrayList<String> = arrayListOf(),
    @SerializedName("totalNumber")
    val totalNumber: Int = 0,
) {
    companion object {
        val EMPTY = LessonResponse("", "", ArrayList(), "", "", 0, "", ArrayList(), 0)
    }
}