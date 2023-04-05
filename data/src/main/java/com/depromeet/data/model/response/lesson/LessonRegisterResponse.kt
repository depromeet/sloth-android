package com.depromeet.data.model.response.lesson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LessonRegisterResponse (
    @SerialName("lessonId")
    val lessonId: Int = 0
) {
    companion object {
        val EMPTY = LessonRegisterResponse(0)
    }
}

