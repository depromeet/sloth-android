package com.depromeet.sloth.data.network.notification

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface NotificationService {
    @POST("api/fcmtoken")
    suspend fun saveFCMToken(@Body notificationSaveRequest: NotificationSaveRequest): Response<String>?

    @PATCH("api/fcmtoken/use")
    suspend fun updateFCMTokenUse(@Body notificationUseRequest: NotificationUseRequest): Response<NotificationUseResponse>?
}