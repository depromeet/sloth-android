package com.depromeet.presentation.model


data class LessonUpdateRequest (
    val categoryId: Int,
    val lessonName: String,
    val price: Int,
    val siteId: Int,
    val totalNumber: Int,
)



