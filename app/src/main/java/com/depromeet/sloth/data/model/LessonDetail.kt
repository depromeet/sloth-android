package com.depromeet.sloth.data.model

data class LessonDetail(
    var alertDays: String?,
    var categoryName: String,
    var currentProgressRate: Int,
    var endDate: ArrayList<String>,
    var goalProgressRate: Int,
    var isFinished: Boolean,
    var lessonId: Int,
    var lessonName: String,
    var message: String,
    var presentNumber: Int,
    var price: Int,
    var remainDay: Int,
    var siteName: String,
    var startDate: ArrayList<String>,
    var totalNumber: Int,
    var wastePrice: Int,
)
