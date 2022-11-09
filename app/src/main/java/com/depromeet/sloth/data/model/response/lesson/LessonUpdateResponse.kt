package com.depromeet.sloth.data.model.response.lesson

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
data class LessonUpdateResponse (
    @SerializedName("categoryId")
    val categoryId: Int = 0,
    @SerializedName("lessonId")
    val lessonId: Int = 0,
    @SerializedName("lessonName")
    val lessonName: String = "",
    @SerializedName("siteId")
    val siteId: Int = 0,
    @SerializedName("totalNumber")
    val totalNumber: Int = 0
) {
    companion object {
        val EMPTY = LessonUpdateResponse(0, 0, "", 0,  0)
    }
}

