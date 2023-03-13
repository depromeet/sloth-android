package com.depromeet.domain.entity


data class UpdateLessonCountEntity (
    val isFinished: Boolean,
    val lessonId: Int,
    val presentNumber: Int,
)