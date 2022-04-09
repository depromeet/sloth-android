package com.depromeet.sloth.data.network.lesson.list

/**
 *   TodayLessonResponse
 *
 *   "categoryName": "string",
 *   "lessonId": 0,
 *   "lessonName": "string",
 *   "presentNumber": 0,
 *   "remainDay": 0,
 *   "siteName": "string",
 *   "untilTodayFinished": false,
 *   "untilTodayNumber": 0
 */

data class LessonTodayResponse (
    var categoryName: String,
    var lessonId: Int,
    var lessonName: String,
    var presentNumber: Int,
    var remainDay: Int,
    var siteName: String,
    var untilTodayFinished: Boolean,
    var untilTodayNumber: Int
) {
    companion object {
        val EMPTY = LessonTodayResponse("", 0, "", 0, 0, "", false, 0)
    }
}