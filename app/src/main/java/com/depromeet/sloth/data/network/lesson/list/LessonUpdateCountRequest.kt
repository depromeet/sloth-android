package com.depromeet.sloth.data.network.lesson.list

import com.google.gson.annotations.SerializedName

class LessonUpdateCountRequest (
    @SerializedName("count")
    private val count: Int,
    @SerializedName("lessonId")
    private val lessonId: Int
)