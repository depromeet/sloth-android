package com.depromeet.sloth.data.network.lesson

import androidx.annotation.Keep

@Keep
data class LessonSite(
    var siteId: Int,
    var siteName: String
) {
    companion object {
        val EMPTY = LessonSite(0, "")
    }
}
