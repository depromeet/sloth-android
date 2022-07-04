package com.depromeet.sloth.data.network.lesson.register

import com.depromeet.sloth.data.model.LessonUpdate

/**
 * RegisterLessonResponse
 *
 * "lessonId": Int
 */

data class LessonRegisterResponse (
    var lessonId: Int = 0
) {
    companion object {
        val EMPTY = LessonRegisterResponse(0)
    }
}