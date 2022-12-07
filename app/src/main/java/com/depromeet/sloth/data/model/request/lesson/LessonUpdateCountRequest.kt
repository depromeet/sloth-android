package com.depromeet.sloth.data.model.request.lesson

import com.google.gson.annotations.SerializedName

data class LessonUpdateCountRequest (
    @SerializedName("count")
    val count: Int,
    @SerializedName("lessonId")
    val lessonId: Int
)