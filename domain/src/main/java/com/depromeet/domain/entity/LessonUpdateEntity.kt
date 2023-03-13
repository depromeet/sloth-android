package com.depromeet.domain.entity


data class LessonUpdateEntity (
    val categoryId: Int = 0,
    val lessonId: Int = 0,
    val lessonName: String = "",
    val siteId: Int = 0,
    val totalNumber: Int = 0
)
