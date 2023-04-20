package com.depromeet.data.service

import com.depromeet.data.model.request.notification.NotificationRegisterRequest
import com.depromeet.data.model.request.notification.NotificationUpdateRequest
import com.depromeet.data.model.response.notification.NotificationFetchResponse
import com.depromeet.data.model.response.notification.NotificationResponse
import retrofit2.Response
import retrofit2.http.*


interface NotificationService {
    @POST("api/fcmtoken")
    suspend fun registerFCMToken(
        @Body notificationRegisterRequest: NotificationRegisterRequest
    ): Response<String>?

    @PATCH("api/fcmtoken/use")
    suspend fun updateFCMTokenUse(
        @Body notificationUpdateRequest: NotificationUpdateRequest
    ): Response<String>?

//    @PATCH("api/fcmtoken/use")
//    suspend fun updateFCMTokenUse(
//        @Body notificationUpdateRequest: NotificationUpdateRequest
//    ): Response<NotificationUpdateResponse>?

    @GET("api/fcmtoken/device/{deviceId}")
    suspend fun fetchFCMToken(
        @Path("deviceId") deviceId: String
    ): Response<NotificationFetchResponse>?

    @GET("/api/alarms")
    suspend fun fetchNotificationList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<List<NotificationResponse>>?

    @PATCH("/api/alarms/{alarmId}/read-time")
    suspend fun updateNotificationState(
        @Path("alarmId") alarmId: Long,
    ): Response<String>?
}