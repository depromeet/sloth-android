package com.depromeet.sloth.data.model

import androidx.annotation.Keep
import com.depromeet.sloth.data.network.lesson.list.LessonTodayResponse

@Keep
data class LessonUpdate (
    var categoryId: Int = 0,
    var lessonId: Int = 0,
    var lessonName: String = "",
    var siteId: Int = 0,
    var totalNumber: Int = 0
) {
    companion object {
        val EMPTY = LessonUpdate(0, 0, "", 0,  0)
    }
}

