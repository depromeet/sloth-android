package com.depromeet.sloth.data.model.request.lesson

import com.google.gson.annotations.SerializedName

data class LessonUpdateCountRequest (
    @SerializedName("count")
    private val count: Int,
    @SerializedName("lessonId")
    private val lessonId: Int
)