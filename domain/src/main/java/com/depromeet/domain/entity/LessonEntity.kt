package com.depromeet.domain.entity


data class LessonEntity (
    val categoryName: String,
    val currentProgressRate: Int,
    val endDate: String,
    val goalProgressRate: Int,
    val isFinished: Boolean,
    val lessonId: Int,
    val lessonName: String,
    val lessonStatus: String,
    val price: Int,
    val remainDay: Int,
    val siteName: String,
    val startDate: String,
    val totalNumber: Int
)