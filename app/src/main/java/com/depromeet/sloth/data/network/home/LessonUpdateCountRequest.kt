package com.depromeet.sloth.data.network.home

import com.google.gson.annotations.SerializedName

class LessonUpdateCountRequest (
    @SerializedName("count")
    private val count: Int,
    @SerializedName("lessonId")
    private val lessonId: Int
)