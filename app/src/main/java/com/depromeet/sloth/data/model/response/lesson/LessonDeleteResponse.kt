package com.depromeet.sloth.data.model.response.lesson

import com.google.gson.annotations.SerializedName


data class LessonDeleteResponse(
    @SerializedName("code")
    var code: String = "",
    @SerializedName("message")
    var message: String = "",
) {
    companion object {
        val EMPTY = LessonDeleteResponse("", "")
    }
}
