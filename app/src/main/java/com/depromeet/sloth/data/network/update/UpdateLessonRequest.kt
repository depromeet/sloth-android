package com.depromeet.sloth.data.network.update

import com.google.gson.annotations.SerializedName

class UpdateLessonRequest (
    @SerializedName("categoryId")
    private val categoryId: Int,
    @SerializedName("lessonName")
    private val lessonName: String,
    @SerializedName("siteId")
    private val siteId: Int,
    @SerializedName("totalNumber")
    private val totalNumber: Int,
)

