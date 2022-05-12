package com.depromeet.sloth.data.network.notification

import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.AccessTokenAuthenticator
import com.depromeet.sloth.data.network.RetrofitServiceGenerator
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val preferenceManager: PreferenceManager
) {
    suspend fun registerFCMToken(
        notificationRequest: NotificationRegisterRequest
    ): NotificationRegisterState<String> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(NotificationService::class.java)
            .registerFCMToken(notificationRequest)?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        NotificationRegisterState.Success(this.body() ?: "")
                    }
                    else -> NotificationRegisterState.Error(Exception(message()))
                }
            } ?: return NotificationRegisterState.Error(Exception("Retrofit Exception"))
    }

    suspend fun updateFCMTokenUse(
        notificationUseRequest: NotificationUseRequest
    ): NotificationUseState<NotificationUseResponse> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(NotificationService::class.java)
            .updateFCMTokenUse(notificationUseRequest)?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        NotificationUseState.Success(this.body() ?: NotificationUseResponse())
                    }
                    else -> NotificationUseState.Error(Exception(message()))
                }
            } ?: return NotificationUseState.Error(Exception("Retrofit Exception"))
    }
}