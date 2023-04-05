package com.depromeet.data.model.response.lesson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LessonSiteResponse(
    @SerialName("siteId")
    val siteId: Int,
    @SerialName("siteName")
    val siteName: String
) {
    companion object {
        val EMPTY = LessonSiteResponse(0, "")
    }
}

