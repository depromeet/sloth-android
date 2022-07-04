package com.depromeet.sloth.data.network.lesson.delete

/**
 * DeleteLessonResponse
 *
 * "code": "string",
 * "message" : "string"
 */

data class LessonDeleteResponse(
    var code: String = "",
    var message: String = "",
) {
    companion object {
        val EMPTY = LessonDeleteResponse("", "")
    }
}
