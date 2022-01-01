package com.depromeet.sloth.data.network.lesson

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime


/**
 * LessonDetailResponse
 *
 * "alertDays": "string",
 * "categoryName": "string",
 * "currentProgressRate": 0,
 * "endDate": "2021-11-04",
 * "goalProgressRate": 0,
 * "isFinished": false,
 * "lessonId": 0,
 * "lessonName": "string",
 * "message": "string",
 * "presentNumber": 0,
 * "price": 0,
 * "remainDay": 0,
 * "siteName": "string",
 * "startDate": "2021-11-04",
 * "totalNumber": 0,
 * "wastePrice": 0
 */


data class LessonDetailResponse (
    var alertDays: String? = "",
    var categoryName: String = "",
    var currentProgressRate: Int = 0,
    var endDate: ArrayList<String> = ArrayList(),
    var goalProgressRate: Int = 0,
    var isFinished: Boolean = false,
    var lessonId: Int = 0,
    var lessonName: String = "",
    var message: String = "",
    var presentNumber: Int = 0,
    var price: Int = 0,
    var remainDay: Int = 0,
    var siteName: String = "",
    var startDate: ArrayList<String> = ArrayList(),
    var totalNumber: Int = 0,
    var wastePrice: Int = 0,
)
