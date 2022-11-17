package com.depromeet.sloth.data.network.service

import com.depromeet.sloth.data.model.request.notification.NotificationRegisterRequest
import com.depromeet.sloth.data.model.request.notification.NotificationUpdateRequest
import com.depromeet.sloth.data.model.response.notification.NotificationFetchResponse
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
}