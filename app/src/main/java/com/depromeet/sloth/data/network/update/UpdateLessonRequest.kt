package com.depromeet.sloth.data.network.update

import com.google.gson.annotations.SerializedName

data class UpdateLessonRequest (
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("lessonName")
    val lessonName: String,
    @SerializedName("siteId")
    val siteId: Int,
    @SerializedName("totalNumber")
    val totalNumber: Int,
)

