package com.depromeet.presentation.model


data class UpdateLessonCount (
    val isFinished: Boolean,
    val lessonId: Int,
    val presentNumber: Int,
)

