package com.depromeet.sloth.data.model

import androidx.annotation.Keep

@Keep
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
) {
    companion object {
        val EMPTY = Lesson("", "", ArrayList(), "", "", 0, "", ArrayList(), 0)
    }
}