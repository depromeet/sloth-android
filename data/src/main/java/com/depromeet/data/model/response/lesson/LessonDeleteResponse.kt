package com.depromeet.data.model.response.lesson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LessonDeleteResponse(
    @SerialName("code")
    val code: Int = 0,
    @SerialName("message")
    val message: String = "",
) {
    companion object {
        val EMPTY = LessonDeleteResponse(0, "")
    }
}


