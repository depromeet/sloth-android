package com.depromeet.domain.entity


data class LessonDetailEntity(
    val alertDays: String? = "",
    val categoryName: String = "",
    val currentProgressRate: Int = 0,
    val endDate: ArrayList<String> = ArrayList(),
    val goalProgressRate: Int = 0,
    val isFinished: Boolean = false,
    val lessonId: Int = 0,
    val lessonName: String = "",
    val message: String = "",
    val presentNumber: Int = 0,
    val price: Int = 0,
    val remainDay: Int = 0,
    val siteName: String = "",
    val startDate: ArrayList<String> = ArrayList(),
    val totalNumber: Int = 0,
    val wastePrice: Int = 0,
)
