package com.depromeet.sloth.data.network.lesson.register

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