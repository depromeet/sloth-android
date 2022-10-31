package com.depromeet.sloth.data.repository

import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.AccessTokenAuthenticator
import com.depromeet.sloth.data.network.RetrofitServiceGenerator
import com.depromeet.sloth.data.network.notification.NotificationService
import com.depromeet.sloth.data.network.notification.fetch.NotificationFetchResponse
import com.depromeet.sloth.data.network.notification.register.NotificationRegisterRequest
import com.depromeet.sloth.data.network.notification.update.NotificationUpdateRequest
import com.depromeet.sloth.ui.common.UiState
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val preferenceManager: PreferenceManager
) : NotificationRepository {
    override suspend fun registerFCMToken(
        notificationRegisterRequest: NotificationRegisterRequest
    ): UiState<String> {
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

                        UiState.Success(this.body() ?: "")
                    }

                    else -> UiState.Error(Exception(message()))
                }
            } ?: return UiState.Error(Exception("Retrofit Exception"))
    }

    override suspend fun updateNotificationStatus(notificationUpdateRequest: NotificationUpdateRequest): UiState<String> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(NotificationService::class.java)
            .updateFCMTokenUse(notificationUpdateRequest)?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        UiState.Success(this.body() ?: "")
                    }

                    else -> UiState.Error(Exception(message()))
                }
            } ?: return UiState.Error(Exception("Retrofit Exception"))
    }

    override suspend fun fetchFCMToken(
        deviceId: String
    ): UiState<NotificationFetchResponse> {
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

                        UiState.Success(this.body() ?: NotificationFetchResponse())
                    }

                    else -> UiState.Error(Exception(message()))
                }
            } ?: return UiState.Error(Exception("Retrofit Exception"))
    }
}