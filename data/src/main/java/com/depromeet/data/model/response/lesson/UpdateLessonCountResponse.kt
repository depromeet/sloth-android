package com.depromeet.data.model.response.lesson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UpdateLessonCountResponse (
    @SerialName("isFinished")
    val isFinished: Boolean,
    @SerialName("lessonId")
    val lessonId: Int,
    @SerialName("presentNumber")
    val presentNumber: Int,
) {
    companion object {
        val EMPTY = UpdateLessonCountResponse(false, -1, -1)
    }
}

