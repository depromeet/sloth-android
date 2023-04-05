package com.depromeet.data.model.request.lesson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LessonUpdateCountRequest (
    @SerialName("count")
    val count: Int,
    @SerialName("lessonId")
    val lessonId: Int
)

