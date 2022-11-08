package com.depromeet.sloth.data.model.response.lesson

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
data class LessonUpdateResponse (
    @SerializedName("categoryId")
    var categoryId: Int = 0,
    @SerializedName("lessonId")
    var lessonId: Int = 0,
    @SerializedName("lessonName")
    var lessonName: String = "",
    @SerializedName("siteId")
    var siteId: Int = 0,
    @SerializedName("totalNumber")
    var totalNumber: Int = 0
) {
    companion object {
        val EMPTY = LessonUpdateResponse(0, 0, "", 0,  0)
    }
}

