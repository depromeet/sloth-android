package com.depromeet.data.model.response.lesson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LessonDeleteResponse(
    @SerialName("code")
    val code: String = "",
    @SerialName("message")
    val message: String = "",
) {
    companion object {
        val EMPTY = LessonDeleteResponse("", "")
    }
}


