package com.depromeet.sloth.data.db.model

/*
    "alertDays": "string",
    "categoryName": "string",
    "currentProgressRate": 0,
    "endDate": "2021-11-02",
    "goalProgressRate": 0,
    "isFinished": false,
    "lessonId": 0,
    "lessonName": "string",
    "message": "string",
    "presentNumber": 0,
    "price": 0,
    "remainDay": 0,
    "siteName": "string",
    "startDate": "2021-11-02",
    "totalNumber": 0,
    "wastePrice": 0
*/

data class LessonDetailModel(
    val alertDays: String?,
    val categoryName: String,
    val currentProgressRate: Int,
    val endDate: String,
    val goalProgressRate: Int,
    val isFinished: Boolean,
    val lessonId: Int,
    val lessonName: String,
    val message: String,
    val presentNumber: Int,
    val price: Int,
    val remainDay: Int,
    val siteName: String,
    val startDate: String,
    val totalNumber: Int,
    val wastePrice: Int
)
