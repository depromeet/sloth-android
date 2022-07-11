package com.depromeet.sloth.data.network.lesson.update

import com.google.gson.annotations.SerializedName

data class LessonUpdateRequest (
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("lessonName")
    val lessonName: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("siteId")
    val siteId: Int,
    @SerializedName("totalNumber")
    val totalNumber: Int,
)

