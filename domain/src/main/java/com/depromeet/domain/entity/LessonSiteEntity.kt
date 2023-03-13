package com.depromeet.domain.entity


data class LessonSiteEntity(
    val siteId: Int,
    val siteName: String
) {
    companion object {
        val EMPTY = LessonSiteEntity(0, "")
    }
}
