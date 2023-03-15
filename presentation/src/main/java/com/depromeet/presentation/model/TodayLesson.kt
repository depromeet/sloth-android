package com.depromeet.presentation.model


data class TodayLesson(
    val lessonId: Int,
    val lessonName: String,
    val untilTodayFinished: Boolean,
    val remainDay: Int,
    val siteName: String,
    val categoryName: String,
    var presentNumber: Int,
    val untilTodayNumber: Int,
    val totalNumber: Int,
)



