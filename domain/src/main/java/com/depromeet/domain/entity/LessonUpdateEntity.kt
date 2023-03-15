package com.depromeet.domain.entity


data class LessonUpdateEntity (
    val categoryId: Int,
    val lessonId: Int,
    val lessonName: String,
    val siteId: Int,
    val totalNumber: Int
)
