package com.depromeet.sloth.data.repository

import com.depromeet.sloth.common.Result
import com.depromeet.sloth.data.model.request.notification.NotificationRegisterRequest
import com.depromeet.sloth.data.model.request.notification.NotificationUpdateRequest
import com.depromeet.sloth.data.model.response.notification.NotificationFetchResponse
import com.depromeet.sloth.data.network.service.NotificationService
import com.depromeet.sloth.data.preferences.Preferences
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val preferences: Preferences,
    private val service: NotificationService
) : NotificationRepository {

    override suspend fun registerFCMToken(
        notificationRegisterRequest: NotificationRegisterRequest
    ): Result<String> {
        service.registerFCMToken(preferences.getAccessToken(), notificationRegisterRequest)
            ?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferences.updateAccessToken(newAccessToken)
                        }
                        Result.Success(this.body() ?: "")
                    }

                    else -> Result.Error(Exception(message()))
                }
            } ?: return Result.Error(Exception("Retrofit Exception"))
    }

    override fun updateNotificationStatus(notificationUpdateRequest: NotificationUpdateRequest) =
        flow {
            emit(Result.Loading)
            val response = service.updateFCMTokenUse(
                preferences.getAccessToken(),
                notificationUpdateRequest
            ) ?: run {
                emit(Result.Error(Exception("Response is null")))
                return@flow
            }
            when (response.code()) {
                200 -> {
                    val newAccessToken = response.headers()["Authorization"] ?: ""
                    if (newAccessToken.isNotEmpty()) {
                        preferences.updateAccessToken(newAccessToken)
                    }
                    Result.Success(response.body() ?: "")
                }

                else -> Result.Error(Exception(response.message()))
            }
        }
            .catch { throwable -> emit(Result.Error(throwable)) }
            .onCompletion { emit(Result.UnLoading) }


    override suspend fun fetchFCMToken(
        deviceId: String
    ): Result<NotificationFetchResponse> {
        service.fetchFCMToken(preferences.getAccessToken(), deviceId)?.run {
            return when (this.code()) {
                200 -> {
                    val newAccessToken = headers()["Authorization"] ?: ""
                    if (newAccessToken.isNotEmpty()) {
                        preferences.updateAccessToken(newAccessToken)
                    }
                    Result.Success(this.body() ?: NotificationFetchResponse())
                }

                else -> Result.Error(Exception(message()))
            }
        } ?: return Result.Error(Exception("Retrofit Exception"))
    }
}