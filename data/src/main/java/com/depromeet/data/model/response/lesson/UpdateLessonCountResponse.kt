package com.depromeet.data.model.response.lesson

import com.google.gson.annotations.SerializedName


data class UpdateLessonCountResponse (
    @SerializedName("isFinished")
    val isFinished: Boolean,
    @SerializedName("lessonId")
    val lessonId: Int,
    @SerializedName("presentNumber")
    val presentNumber: Int,
) {
    companion object {
        val EMPTY = UpdateLessonCountResponse(false, -1, -1)
    }
}

