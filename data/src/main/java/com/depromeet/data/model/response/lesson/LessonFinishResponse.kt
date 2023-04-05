package com.depromeet.data.model.response.lesson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LessonFinishResponse (
    @SerialName("isFinished")
    val isFinished: Boolean
) {
    companion object {
        val EMPTY = LessonFinishResponse(false)
    }
}

