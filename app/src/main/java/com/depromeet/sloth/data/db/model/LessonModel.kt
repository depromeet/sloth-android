package com.depromeet.sloth.data.db.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LessonModel(
    val alertDays: String?,
    val categoryId: Int,
    val endDate: String,
    val lessonName: String,
    val message: String?,
    val price: Int,
    val siteId: Int,
    val startDate: String,
    val totalNumber: Int
): Parcelable
