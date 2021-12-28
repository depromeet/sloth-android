package com.depromeet.sloth.data.network.lesson

/**
 *   AllLessonResponse
 *
 *   "categoryName": "string",
 *   "currentProgressRate": 0,
 *   "endDate": "string",
 *   "goalProgressRate": 0,
 *   "isFinished": false,
 *   "lessonId": 0,
 *   "lessonName": "string",
 *   "lessonStatus": "CURRENT",
 *   "price": 0,
 *   "remainDay": 0,
 *   "siteName": "string",
 *   "startDate": "string",
 *   "totalNumber": 0
 */

data class LessonAllResponse (
    var categoryName: String = "",
    var currentProgressRate: Int = 0,
    var endDate: String = "",
    var goalProgressRate: Int = 0,
    var isFinished: Boolean = false,
    var lessonId: Int = 0,
    var lessonName: String = "",
    var lessonStatus: String = "",
    var price: Int = 0,
    var remainDay: Int = 0,
    var siteName: String = "",
    var startDate: String = "",
    var totalNumber: Int = 0
) {
    companion object {
        val EMPTY = LessonAllResponse("", 0, "", 0, false, 0, "", "", 0, 0, "", "", 0)
    }
}