package com.depromeet.sloth.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class LessonDetail (
    var alertDays: String? = "",
    var categoryName: String = "",
    var currentProgressRate: Int = 0,
    var endDate: ArrayList<String> = ArrayList(),
    var goalProgressRate: Int = 0,
    var isFinished: Boolean = false,
    var lessonId: Int = 0,
    var lessonName: String = "",
    var message: String = "",
    var presentNumber: Int = 0,
    var price: Int = 0,
    var remainDay: Int = 0,
    var siteName: String = "",
    var startDate: ArrayList<String> = ArrayList(),
    var totalNumber: Int = 0,
    var wastePrice: Int = 0,
): Parcelable {
    companion object {
        val EMPTY = LessonDetail("", "", 0, ArrayList(),  0,false, 0, "", "", 0,0,0,"",ArrayList(),0,0)
    }
}
