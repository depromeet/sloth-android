package com.depromeet.domain.entity


data class LessonRegisterRequestEntity(
    val alertDays: String?,
    val categoryId: Int,
    val endDate: String,
    val lessonName: String,
    val message: String?,
    val price: Int,
    val siteId: Int,
    val startDate: String,
    val totalNumber: Int,
)