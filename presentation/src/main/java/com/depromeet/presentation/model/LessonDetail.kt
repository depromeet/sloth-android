package com.depromeet.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class LessonDetail(
    val alertDays: String? = "",
    val categoryName: String = "",
    val currentProgressRate: Int = 0,
    val endDate: ArrayList<Int> = ArrayList(),
    val goalProgressRate: Int = 0,
    val isFinished: Boolean = false,
    val lessonId: String = "",
    val lessonName: String = "",
    val message: String = "",
    val presentNumber: Int = 0,
    val price: Int = 0,
    val remainDay: Int = 0,
    val siteName: String = "",
    val startDate: ArrayList<Int> = ArrayList(),
    val totalNumber: Int = 0,
    val wastePrice: Int = 0,
) : Parcelable


