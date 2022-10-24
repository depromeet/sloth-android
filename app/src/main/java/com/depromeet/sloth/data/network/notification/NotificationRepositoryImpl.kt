package com.depromeet.sloth.data.network.notification

import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.AccessTokenAuthenticator
import com.depromeet.sloth.data.network.RetrofitServiceGenerator
import com.depromeet.sloth.data.network.notification.fetch.NotificationFetchResponse
import com.depromeet.sloth.data.network.notification.register.NotificationRegisterRequest
import com.depromeet.sloth.data.network.notification.update.NotificationUseResponse
import com.depromeet.sloth.data.network.notification.update.NotificationUseRequest
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val preferenceManager: PreferenceManager
): NotificationRepository {
    override suspend fun registerFCMToken(
        notificationRegisterRequest: NotificationRegisterRequest
    ): NotificationState<String> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(NotificationService::class.java)
            .registerFCMToken(notificationRegisterRequest)?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        NotificationState.Success(this.body() ?: "")
                    }
                    else -> NotificationState.Error(Exception(message()))
                }
            } ?: return NotificationState.Error(Exception("Retrofit Exception"))
    }

    override suspend fun updateFCMTokenUse(
        notificationUseRequest: NotificationUseRequest
    ): NotificationState<NotificationUseResponse> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(NotificationService::class.java)
            .updateFCMToken(notificationUseRequest)?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        NotificationState.Success(this.body() ?: NotificationUseResponse())
                    }
                    else -> NotificationState.Error(Exception(message()))
                }
            } ?: return NotificationState.Error(Exception("Retrofit Exception"))
    }

    override suspend fun fetchFCMToken(
        deviceId: String
    ): NotificationState<NotificationFetchResponse> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(NotificationService::class.java)
            .fetchFCMToken(deviceId)?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        NotificationState.Success(this.body() ?: NotificationFetchResponse())
                    }

                    else -> NotificationState.Error(Exception(message()))
                }
            } ?: return NotificationState.Error(Exception("Retrofit Exception"))
    }
}