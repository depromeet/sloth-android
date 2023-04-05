package com.depromeet.data.model.response.lesson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LessonUpdateResponse (
    @SerialName("categoryId")
    val categoryId: Int = 0,
    @SerialName("lessonId")
    val lessonId: Int = 0,
    @SerialName("lessonName")
    val lessonName: String = "",
    @SerialName("siteId")
    val siteId: Int = 0,
    @SerialName("totalNumber")
    val totalNumber: Int = 0
) {
    companion object {
        val EMPTY = LessonUpdateResponse(0, 0, "", 0,  0)
    }
}



