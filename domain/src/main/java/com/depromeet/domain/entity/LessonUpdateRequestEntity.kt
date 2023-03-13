package com.depromeet.domain.entity


data class LessonUpdateRequestEntity (
    val categoryId: Int,
    val lessonName: String,
    val price: Int,
    val siteId: Int,
    val totalNumber: Int,
)

