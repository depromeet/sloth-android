package com.depromeet.domain.entity


data class LessonDetailEntity(
    val alertDays: String?,
    val categoryName: String,
    val currentProgressRate: Int,
    val endDate: ArrayList<Int>,
    val goalProgressRate: Int,
    val isFinished: Boolean,
    val lessonId: Int,
    val lessonName: String,
    val message: String,
    val presentNumber: Int,
    val price: Int,
    val remainDay: Int,
    val siteName: String,
    val startDate: ArrayList<Int>,
    val totalNumber: Int,
    val wastePrice: Int,
)
