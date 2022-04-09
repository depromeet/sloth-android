package com.depromeet.sloth.data.network.lesson.list

import com.google.gson.annotations.SerializedName

data class LessonUpdateCountResponse (
    @SerializedName("isFinished")
    var isFinished: Boolean,
    @SerializedName("lessonId")
    val lessonId: Int,
    @SerializedName("presentNumber")
    var presentNumber: Int,
    @SerializedName("weeklyFinished")
    var weeklyFinished: Boolean
) {
    companion object {
        val EMPTY = LessonUpdateCountResponse(false, -1, -1, false)
    }
}