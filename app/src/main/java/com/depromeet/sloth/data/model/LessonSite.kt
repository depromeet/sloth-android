package com.depromeet.sloth.data.model

data class LessonSite(
    var siteId: Int,
    var siteName: String
) {
    companion object {
        val EMPTY = LessonSite(0, "")
    }
}
