package com.depromeet.sloth.data.network.service

import com.depromeet.sloth.data.model.response.notification.NotificationFetchResponse
import com.depromeet.sloth.data.model.request.notification.NotificationRegisterRequest
import com.depromeet.sloth.data.model.request.notification.NotificationUpdateRequest
import retrofit2.Response
import retrofit2.http.*

interface NotificationService {
    @POST("api/fcmtoken")
    suspend fun registerFCMToken(
        @Header("Authorization") accessToken: String?,
        @Body notificationRegisterRequest: NotificationRegisterRequest
    ): Response<String>?

    @PATCH("api/fcmtoken/use")
    suspend fun updateFCMTokenUse(
        @Header("Authorization") accessToken: String?,
        @Body notificationUpdateRequest: NotificationUpdateRequest
    ): Response<String>?

    @GET("api/fcmtoken/device/{deviceId}")
    suspend fun fetchFCMToken(
        @Header("Authorization") accessToken: String?,
        @Path("deviceId") deviceId: String
    ): Response<NotificationFetchResponse>?
}