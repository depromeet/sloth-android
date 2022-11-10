package com.depromeet.sloth.data.model.response.lesson

import com.google.gson.annotations.SerializedName


data class LessonFinishResponse (
    @SerializedName("isFinished")
    val isFinished: Boolean
) {
    companion object {
        val EMPTY = LessonFinishResponse(false)
    }
}