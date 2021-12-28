package com.depromeet.sloth.data.network.lesson

import com.google.gson.annotations.SerializedName

data class LessonUpdateInfoRequest (
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("lessonName")
    val lessonName: String,
    @SerializedName("siteId")
    val siteId: Int,
    @SerializedName("totalNumber")
    val totalNumber: Int,
)

