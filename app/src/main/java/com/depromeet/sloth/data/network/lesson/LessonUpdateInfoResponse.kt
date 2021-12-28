package com.depromeet.sloth.data.network.lesson

/**
 *   LoginGoogleResponse
 *
 *  "categoryId": 0,
 *  "lessonId": 0,
 *  "lessonName": "string",
 *  "siteId": 0
 *  "totalNumber": 0
 */

data class LessonUpdateInfoResponse (
    var categoryId: Int = 0,
    var lessonId: Int = 0,
    var lessonName: String = "",
    var siteId: Int = 0,
    var totalNumber: Int = 0
)
