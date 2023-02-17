package com.depromeet.sloth.data.model.response.lesson

import com.google.gson.annotations.SerializedName


data class UpdateLessonCountResponse (
    @SerializedName("isFinished")
    val isFinished: Boolean,
    @SerializedName("lessonId")
    val lessonId: Int,
    @SerializedName("presentNumber")
    val presentNumber: Int,
    @SerializedName("weeklyFinished")
    val weeklyFinished: Boolean
) {
    companion object {
        val EMPTY = UpdateLessonCountResponse(false, -1, -1, false)
    }
}