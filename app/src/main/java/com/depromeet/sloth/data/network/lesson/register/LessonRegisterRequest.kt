package com.depromeet.sloth.data.network.lesson.register

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LessonRegisterRequest(
    @SerializedName("alertDays")
    val alertDays: String?,
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("endDate")
    val endDate: String,
    @SerializedName("lessonName")
    val lessonName: String,
    @SerializedName("message")
    val message: String?,
    @SerializedName("price")
    val price: Int,
    @SerializedName("siteId")
    val siteId: Int,
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("totalNumber")
    val totalNumber: Int,
) : Parcelable