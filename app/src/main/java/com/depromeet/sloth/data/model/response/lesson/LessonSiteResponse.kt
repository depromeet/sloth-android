package com.depromeet.sloth.data.model.response.lesson

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class LessonSiteResponse(
    @SerializedName("siteId")
    val siteId: Int,
    @SerializedName("siteName")
    val siteName: String
) {
    companion object {
        val EMPTY = LessonSiteResponse(0, "")
    }
}
