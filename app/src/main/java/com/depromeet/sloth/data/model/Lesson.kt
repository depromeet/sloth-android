package com.depromeet.sloth.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lesson (
    val alertDays: String?,
    val categoryName: String,
    val endDate: ArrayList<String>,
    val lessonName: String,
    val message: String?,
    val price: Int,
    val siteName: String,
    val startDate: ArrayList<String>,
    val totalNumber: Int
): Parcelable