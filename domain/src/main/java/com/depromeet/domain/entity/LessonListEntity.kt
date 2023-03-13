package com.depromeet.domain.entity


data class LessonListEntity (
    val categoryName: String = "",
    val currentProgressRate: Int = 0,
    val endDate: String = "",
    val goalProgressRate: Int = 0,
    val isFinished: Boolean = false,
    val lessonId: Int = 0,
    val lessonName: String = "",
    val lessonStatus: String = "",
    val price: Int = 0,
    val remainDay: Int = 0,
    val siteName: String = "",
    val startDate: String = "",
    val totalNumber: Int = 0
)