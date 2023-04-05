package com.depromeet.data.model.request.lesson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LessonRegisterRequest(
    @SerialName("alertDays")
    val alertDays: String?,
    @SerialName("categoryId")
    val categoryId: Int,
    @SerialName("endDate")
    val endDate: String,
    @SerialName("lessonName")
    val lessonName: String,
    @SerialName("message")
    val message: String?,
    @SerialName("price")
    val price: Int,
    @SerialName("siteId")
    val siteId: Int,
    @SerialName("startDate")
    val startDate: String,
    @SerialName("totalNumber")
    val totalNumber: Int,
)

