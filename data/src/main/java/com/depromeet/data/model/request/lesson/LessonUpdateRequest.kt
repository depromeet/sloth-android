package com.depromeet.data.model.request.lesson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// import com.google.gson.annotations.SerializedName

@Serializable
data class LessonUpdateRequest (
    @SerialName("categoryId")
    val categoryId: Int,
    @SerialName("lessonName")
    val lessonName: String,
    @SerialName("price")
    val price: Int,
    @SerialName("siteId")
    val siteId: Int,
    @SerialName("totalNumber")
    val totalNumber: Int,
)


