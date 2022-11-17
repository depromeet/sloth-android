package com.depromeet.sloth.data.repository

import com.depromeet.sloth.common.Result
import com.depromeet.sloth.data.model.request.notification.NotificationRegisterRequest
import com.depromeet.sloth.data.model.request.notification.NotificationUpdateRequest
import com.depromeet.sloth.data.model.response.notification.NotificationFetchResponse
import com.depromeet.sloth.data.network.service.NotificationService
import com.depromeet.sloth.data.preferences.Preferences
import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
import com.depromeet.sloth.util.KEY_AUTHORIZATION
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val preferences: Preferences,
    private val notificationService: NotificationService,
) : NotificationRepository {

    override suspend fun registerFCMToken(
        notificationRegisterRequest: NotificationRegisterRequest
    ): Result<String> {
        notificationService.registerFCMToken(notificationRegisterRequest)
            ?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                        if (newAccessToken.isNotEmpty()) {
                            preferences.updateAccessToken(newAccessToken)
                        }
                        Result.Success(this.body() ?: DEFAULT_STRING_VALUE)
                    }
                    401 -> {
                        preferences.removeAuthToken()
                        Result.Unauthorized(Exception(message()))
                    }
                    else -> Result.Error(Exception(message()))
                }
            } ?: return Result.Error(Exception("Retrofit Exception"))
    }

        override fun updateNotificationStatus(notificationUpdateRequest: NotificationUpdateRequest) =
        flow {
            emit(Result.Loading)
            val response = notificationService.updateFCMTokenUse(notificationUpdateRequest) ?: run {
                emit(Result.Error(Exception("Response is null")))
                return@flow
            }
            when (response.code()) {
                200 -> {
                    val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                    if (newAccessToken.isNotEmpty()) {
                        preferences.updateAccessToken(newAccessToken)
                    }
                    Result.Success(response.body() ?: DEFAULT_STRING_VALUE)
                }
                401 -> {
                    preferences.removeAuthToken()
                    emit(Result.Unauthorized(Exception(response.message())))
                }

                else -> Result.Error(Exception(response.message()))
            }
        }
            .catch { throwable -> emit(Result.Error(throwable)) }
            .onCompletion { emit(Result.UnLoading) }


// response 형식 바뀌면 해당 메소드 사용
//    override fun updateNotificationStatus(notificationUpdateRequest: NotificationUpdateRequest) =
//        flow {
//            emit(Result.Loading)
//            val response = notificationService.updateFCMTokenUse(
//                notificationUpdateRequest
//            ) ?: run {
//                emit(Result.Error(Exception("Response is null")))
//                return@flow
//            }
//            when (response.code()) {
//                200 -> {
//                    val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
//                    if (newAccessToken.isNotEmpty()) {
//                        preferences.updateAccessToken(newAccessToken)
//                    }
//                    Result.Success(response.body() ?: NotificationUpdateResponse.EMPTY)
//                }
//                401 -> {
//                    preferences.removeAuthToken()
//                    emit(Result.Unauthorized(Exception(response.message())))
//                }
//
//                else -> Result.Error(Exception(response.message()))
//            }
//        }
//            .catch { throwable -> emit(Result.Error(throwable)) }
//            .onCompletion { emit(Result.UnLoading) }


    override suspend fun fetchFCMToken(
        deviceId: String
    ): Result<NotificationFetchResponse> {
        notificationService.fetchFCMToken(deviceId)?.run {
            return when (this.code()) {
                200 -> {
                    val newAccessToken = headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                    if (newAccessToken.isNotEmpty()) {
                        preferences.updateAccessToken(newAccessToken)
                    }
                    Result.Success(this.body() ?: NotificationFetchResponse.EMPTY)
                }
                401 -> {
                    preferences.removeAuthToken()
                    Result.Unauthorized(Exception(message()))
                }
                else -> Result.Error(Exception(message()))
            }
        } ?: return Result.Error(Exception("Retrofit Exception"))
    }
}