package com.depromeet.sloth.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lesson (
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