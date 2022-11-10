package com.depromeet.sloth.data.model.response.lesson

import com.google.gson.annotations.SerializedName


data class LessonRegisterResponse (
    @SerializedName("lessonId")
    val lessonId: Int = 0
) {
    companion object {
        val EMPTY = LessonRegisterResponse(0)
    }
}