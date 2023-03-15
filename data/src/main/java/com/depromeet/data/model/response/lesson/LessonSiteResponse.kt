package com.depromeet.data.model.response.lesson

import com.google.gson.annotations.SerializedName


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

