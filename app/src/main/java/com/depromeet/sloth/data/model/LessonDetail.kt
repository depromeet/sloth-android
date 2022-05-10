package com.depromeet.sloth.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LessonDetail (
    var alertDays: String?,
    var categoryName: String,
    var currentProgressRate: Float,
    var endDate: ArrayList<String>,
    var goalProgressRate: Float,
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
    var wastePrice: Int
): Parcelable
