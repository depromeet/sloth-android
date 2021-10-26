package com.depromeet.sloth.data.model

import com.google.gson.annotations.SerializedName

data class LessonModel(
    val alertDays: String,
    val categoryId: Int,
    val endDate: String,
    val lessonName: String,
    val memberId: Int,
    val message: String,
    val price: Int,
    val siteId: Int,
    val startDate: String,
    val totalNumber: Int
)
