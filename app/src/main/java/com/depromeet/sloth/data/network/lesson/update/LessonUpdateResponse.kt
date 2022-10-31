package com.depromeet.sloth.data.network.lesson.update

import androidx.annotation.Keep

@Keep
data class LessonUpdateResponse (
    var categoryId: Int = 0,
    var lessonId: Int = 0,
    var lessonName: String = "",
    var siteId: Int = 0,
    var totalNumber: Int = 0
) {
    companion object {
        val EMPTY = LessonUpdateResponse(0, 0, "", 0,  0)
    }
}

