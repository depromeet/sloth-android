package com.depromeet.data.model.request.lesson

import com.google.gson.annotations.SerializedName


data class LessonRegisterRequest(
    @SerializedName("alertDays")
    val alertDays: String?,
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("endDate")
    val endDate: String,
    @SerializedName("lessonName")
    val lessonName: String,
    @SerializedName("message")
    val message: String?,
    @SerializedName("price")
    val price: Int,
    @SerializedName("siteId")
    val siteId: Int,
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("totalNumber")
    val totalNumber: Int,
)

