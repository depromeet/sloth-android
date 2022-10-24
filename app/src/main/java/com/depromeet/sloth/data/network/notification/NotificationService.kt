package com.depromeet.sloth.data.network.notification

import com.depromeet.sloth.data.network.notification.fetch.NotificationFetchResponse
import com.depromeet.sloth.data.network.notification.register.NotificationRegisterRequest
import com.depromeet.sloth.data.network.notification.update.NotificationUseResponse
import com.depromeet.sloth.data.network.notification.update.NotificationUseRequest
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
    suspend fun updateFCMToken(@Body notificationUseRequest: NotificationUseRequest): Response<NotificationUseResponse>?

    @GET("api/fcmtoken/device/{deviceId}")
    suspend fun fetchFCMToken(@Path("deviceId") deviceId: String): Response<NotificationFetchResponse>?
}