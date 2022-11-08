package com.depromeet.sloth.data.model.response.lesson

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class LessonDetailResponse (
    @SerializedName("alertDays")
    var alertDays: String? = "",
    @SerializedName("categoryName")
    var categoryName: String = "",
    @SerializedName("currentProgressRate")
    var currentProgressRate: Int = 0,
    @SerializedName("endDate")
    var endDate: ArrayList<String> = ArrayList(),
    @SerializedName("goalProgressRate")
    var goalProgressRate: Int = 0,
    @SerializedName("isFinished")
    var isFinished: Boolean = false,
    @SerializedName("lessonId")
    var lessonId: Int = 0,
    @SerializedName("lessonName")
    var lessonName: String = "",
    @SerializedName("message")
    var message: String = "",
    @SerializedName("presentNumber")
    var presentNumber: Int = 0,
    @SerializedName("price")
    var price: Int = 0,
    @SerializedName("remainDay")
    var remainDay: Int = 0,
    @SerializedName("siteName")
    var siteName: String = "",
    @SerializedName("startDate")
    var startDate: ArrayList<String> = ArrayList(),
    @SerializedName("totalNumber")
    var totalNumber: Int = 0,
    @SerializedName("wastePrice")
    var wastePrice: Int = 0,
): Parcelable {
    companion object {
        val EMPTY = LessonDetailResponse("", "", 0, ArrayList(),  0,false, 0, "", "", 0,0,0,"",ArrayList(),0,0)
    }
}
