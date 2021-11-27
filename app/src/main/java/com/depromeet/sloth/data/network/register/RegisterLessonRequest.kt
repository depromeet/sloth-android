package com.depromeet.sloth.data.network.register

import com.google.gson.annotations.SerializedName

class RegisterLessonRequest (
    @SerializedName("alertDays")
    private val alertDays: String?,
    @SerializedName("categoryId")
    private val categoryId: Int,
    @SerializedName("endDate")
    private val endDate: String,
    @SerializedName("lessonName")
    private val lessonName: String,
    @SerializedName("message")
    private val message: String?,
    @SerializedName("price")
    private val price: Int,
    @SerializedName("siteId")
    private val siteId: Int,
    @SerializedName("startDate")
    private val startDate: String,
    @SerializedName("totalNumber")
    private val totalNumber: Int
)