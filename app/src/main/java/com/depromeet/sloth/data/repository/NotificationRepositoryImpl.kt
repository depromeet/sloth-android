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

    override fun registerFCMToken(notificationRegisterRequest: NotificationRegisterRequest) =
        flow {
            emit(Result.Loading)
            val response = notificationService.registerFCMToken(notificationRegisterRequest) ?: run {
                emit(Result.Error(Exception("Response is null")))
                return@flow
            }
            when (response.code()) {
                200 -> {
                    val newAccessToken =
                        response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                    if (newAccessToken.isNotEmpty()) {
                        preferences.updateAccessToken(newAccessToken)
                    }
                    emit(Result.Success(response.body() ?: DEFAULT_STRING_VALUE))
                }
                401 -> {
                    emit(Result.Unauthorized(Exception(response.message())))
                }

                else -> emit(Result.Error(Exception(response.message())))
            }
        }
            .catch { throwable -> emit(Result.Error(throwable)) }
            .onCompletion { emit(Result.UnLoading) }


    override fun updateNotificationStatus(notificationUpdateRequest: NotificationUpdateRequest) =
        flow {
            emit(Result.Loading)
            val response = notificationService.updateFCMTokenUse(notificationUpdateRequest) ?: run {
                emit(Result.Error(Exception("Response is null")))
                return@flow
            }
            when (response.code()) {
                200 -> {
                    val newAccessToken =
                        response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                    if (newAccessToken.isNotEmpty()) {
                        preferences.updateAccessToken(newAccessToken)
                    }
                    emit(Result.Success(response.body() ?: DEFAULT_STRING_VALUE))
                }
                401 -> {
                    emit(Result.Unauthorized(Exception(response.message())))
                }

                else -> emit(Result.Error(Exception(response.message())))
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
//                    emit(Result.Success(response.body() ?: NotificationUpdateResponse.EMPTY))
//                }
//                401 -> {
//                    emit(Result.Unauthorized(Exception(response.message())))
//                }
//
//                else -> emit(Result.Error(Exception(response.message())))
//            }
//        }
//            .catch { throwable -> emit(Result.Error(throwable)) }
//            .onCompletion { emit(Result.UnLoading) }


    //TODO 안드로이드 의존성을 가지고 있는 datasource 를 만들어 거기서 devideId 를 주입
    override fun fetchFCMToken(deviceId: String) = flow {
        emit(Result.Loading)
        val response = notificationService.fetchFCMToken(deviceId) ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: NotificationFetchResponse.EMPTY))
            }
            401 -> {
                emit(Result.Unauthorized(Exception(response.message())))
            }

            else -> emit(Result.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }
        .onCompletion { emit(Result.UnLoading) }
}