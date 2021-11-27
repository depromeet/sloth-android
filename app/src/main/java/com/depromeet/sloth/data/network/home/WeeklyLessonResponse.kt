package com.depromeet.sloth.data.network.home

/**
 *   WeeklyLessonResponse
 *
 *   "categoryName": "string",
 *   "lessonId": 0,
 *   "lessonName": "string",
 *   "presentNumber": 0,
 *   "remainDay": 0,
 *   "remainNumber": 0,
 *   "siteName": "string",
 *   "weeklyFinished": false
 */

data class WeeklyLessonResponse (
    var categoryName: String = "",
    var lessonId: Int = 0,
    var lessonName: String = "",
    var presentNumber: Int = 0,
    var remainDay: Int = 0,
    var remainNumber: Int = 0,
    var siteName: String = "",
    var weeklyFinished: Boolean = false
)