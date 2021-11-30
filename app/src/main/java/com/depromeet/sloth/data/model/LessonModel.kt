package com.depromeet.sloth.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LessonModel(
    var alertDays: String?,
    var categoryId: Int,
    var endDate: String,
    var lessonName: String,
    var message: String,
    var price: Int,
    var siteId: Int,
    var startDate: String,
    var totalNumber: Int
): Parcelable
