package com.depromeet.sloth.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Lesson(
    val alertDays: String? = "",
    val categoryName: String = "",
    val endDate: ArrayList<String> = arrayListOf(),
    val lessonName: String = "",
    val message: String? = "",
    val price: Int = 0,
    val siteName: String = "",
    val startDate: ArrayList<String> = arrayListOf(),
    val totalNumber: Int = 0,
) : Parcelable {
    companion object {
        val EMPTY = Lesson("", "", ArrayList(), "", "", 0, "", ArrayList(), 0)
    }
}