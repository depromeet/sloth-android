package com.depromeet.sloth.data.model.response.lesson

import com.google.gson.annotations.SerializedName


data class LessonDeleteResponse(
    @SerializedName("code")
    val code: String = "",
    @SerializedName("message")
    val message: String = "",
) {
    companion object {
        val EMPTY = LessonDeleteResponse("", "")
    }
}
