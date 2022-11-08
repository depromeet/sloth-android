package com.depromeet.sloth.data.network.service

import com.depromeet.sloth.data.model.response.notification.NotificationFetchResponse
import com.depromeet.sloth.data.model.request.notification.NotificationRegisterRequest
import com.depromeet.sloth.data.model.request.notification.NotificationUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationService {
    @POST("api/fcmtoken")
    suspend fun registerFCMToken(@Body notificationRegisterRequest: NotificationRegisterRequest): Response<String>?

    @PATCH("api/fcmtoken/use")
    suspend fun updateFCMTokenUse(@Body notificationUpdateRequest: NotificationUpdateRequest): Response<String>?

    @GET("api/fcmtoken/device/{deviceId}")
    suspend fun fetchFCMToken(@Path("deviceId") deviceId: String): Response<NotificationFetchResponse>?
}