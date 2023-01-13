package com.depromeet.sloth.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LessonDetail(
    val lessonId: String = "",
    val categoryName: String = "",
    val currentProgressRate: Int = 0,
    val endDate: ArrayList<String> = ArrayList(),
    val goalProgressRate: Int = 0,
    val isFinished: Boolean = false,
    val lessonName: String = "",
    val message: String = "",
    val presentNumber: Int = 0,
    val price: Int = 0,
    val remainDay: Int = 0,
    val siteName: String = "",
    val startDate: ArrayList<String> = ArrayList(),
    val totalNumber: Int = 0,
    val wastePrice: Int = 0,
): Parcelable
