package com.depromeet.sloth.data.network.notification

import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.RetrofitServiceGenerator
import com.depromeet.sloth.data.network.member.MemberUpdateInfoResponse
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val preferenceManager: PreferenceManager
) {
    suspend fun saveFCMToken(
        notificationRequest: NotificationSaveRequest
    ): NotificationSaveState<String> {
        RetrofitServiceGenerator.build(preferenceManager.getAccessToken())
            .create(NotificationService::class.java)
            .saveFCMToken(notificationRequest)?.run {
                return when (this.code()) {
                    200 -> NotificationSaveState.Success(this.body() ?: "")
                    201 -> NotificationSaveState.Created
                    401 -> {
                        val refreshToken = preferenceManager.getRefreshToken()
                        RetrofitServiceGenerator.build(refreshToken)
                            .create(NotificationService::class.java)
                            .saveFCMToken(notificationRequest)?.run {
                                when (code()) {
                                    200 -> {
                                        val newAccessToken = headers()["Authorization"] ?: "EMPTY"
                                        preferenceManager.updateAccessToken(newAccessToken)
                                        NotificationSaveState.Success(body() ?: "")
                                    }
                                    else -> NotificationSaveState.Unauthorized(Exception("Authentication Failed Exception"))
                                }
                            } ?: NotificationSaveState.Error(Exception("Uncaught Exception"))
                    }
                    403 -> NotificationSaveState.Forbidden
                    404 -> NotificationSaveState.NotFound
                    else -> NotificationSaveState.Error(Exception("Uncaught Exception"))
                }
            } ?: return NotificationSaveState.Error(Exception("Retrofit Exception"))
    }

    suspend fun updateFCMTokenUse(
        notificationUseRequest: NotificationUseRequest
    ): NotificationUseState<NotificationUseResponse> {
        RetrofitServiceGenerator.build(preferenceManager.getAccessToken())
            .create(NotificationService::class.java)
            .updateFCMTokenUse(notificationUseRequest)?.run {
                return when (this.code()) {
                    200 -> NotificationUseState.Success(this.body() ?: NotificationUseResponse())
                    204 -> NotificationUseState.NoContent
                    401 -> {
                        val refreshToken = preferenceManager.getRefreshToken()
                        RetrofitServiceGenerator.build(refreshToken)
                            .create(NotificationService::class.java)
                            .updateFCMTokenUse(notificationUseRequest)?.run {
                                when (code()) {
                                    200 -> {
                                        val newAccessToken = headers()["Authorization"] ?: "EMPTY"
                                        preferenceManager.updateAccessToken(newAccessToken)
                                        NotificationUseState.Success(body() ?: NotificationUseResponse())
                                    }
                                    else -> NotificationUseState.Unauthorized(Exception("Authentication Failed Exception"))
                                }
                            } ?: NotificationUseState.Error(Exception("Uncaught Exception"))
                    }
                    403 -> NotificationUseState.Forbidden
                    else -> NotificationUseState.Error(Exception("Uncaught Exception"))
                }
            } ?: return NotificationUseState.Error(Exception("Retrofit Exception"))
    }
}