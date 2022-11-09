package com.depromeet.sloth.data.model.response.lesson

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class LessonDetailResponse (
    @SerializedName("alertDays")
    val alertDays: String? = "",
    @SerializedName("categoryName")
    val categoryName: String = "",
    @SerializedName("currentProgressRate")
    val currentProgressRate: Int = 0,
    @SerializedName("endDate")
    val endDate: ArrayList<String> = ArrayList(),
    @SerializedName("goalProgressRate")
    val goalProgressRate: Int = 0,
    @SerializedName("isFinished")
    val isFinished: Boolean = false,
    @SerializedName("lessonId")
    val lessonId: Int = 0,
    @SerializedName("lessonName")
    val lessonName: String = "",
    @SerializedName("message")
    val message: String = "",
    @SerializedName("presentNumber")
    val presentNumber: Int = 0,
    @SerializedName("price")
    val price: Int = 0,
    @SerializedName("remainDay")
    val remainDay: Int = 0,
    @SerializedName("siteName")
    val siteName: String = "",
    @SerializedName("startDate")
    val startDate: ArrayList<String> = ArrayList(),
    @SerializedName("totalNumber")
    val totalNumber: Int = 0,
    @SerializedName("wastePrice")
    val wastePrice: Int = 0,
): Parcelable {
    companion object {
        val EMPTY = LessonDetailResponse("", "", 0, ArrayList(),  0,false, 0, "", "", 0,0,0,"",ArrayList(),0,0)
    }
}
