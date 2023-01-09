package com.depromeet.sloth.data.repository

import android.content.Context
import android.provider.Settings
import com.depromeet.sloth.data.model.request.notification.NotificationRegisterRequest
import com.depromeet.sloth.data.model.request.notification.NotificationUpdateRequest
import com.depromeet.sloth.data.model.response.notification.NotificationFetchResponse
import com.depromeet.sloth.data.network.service.NotificationService
import com.depromeet.sloth.data.preferences.PreferenceManager
import com.depromeet.sloth.domain.repository.NotificationRepository
import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
import com.depromeet.sloth.util.INTERNET_CONNECTION_ERROR
import com.depromeet.sloth.util.KEY_AUTHORIZATION
import com.depromeet.sloth.util.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: PreferenceManager,
    private val notificationService: NotificationService,
) : NotificationRepository {

    private val deviceId: String by lazy {
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    override fun registerNotificationToken(fcmToken: String) =
        flow {
            emit(Result.Loading)
            val response = notificationService.registerFCMToken(NotificationRegisterRequest(deviceId, fcmToken)) ?: run {
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
                else -> emit(Result.Error(Exception(response.message()), response.code()))
            }
        }
            .catch { throwable -> emit(Result.Error(throwable)) }


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
                else -> emit(Result.Error(Exception(response.message()), response.code()))
            }
        }
            .catch { throwable ->
                when(throwable) {
                    is IOException -> {
                        // Handle Internet Connection Error
                        emit(Result.Error(Exception(INTERNET_CONNECTION_ERROR)))
                    }
                    else -> {
                        // Handle Other Error
                        emit(Result.Error(throwable))
                    }
                }
            }


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
//                else -> emit(Result.Error(Exception(response.message())))
//            }
//        }
//            .catch { throwable -> emit(Result.Error(throwable)) }


    override fun fetchNotificationToken(deviceId: String) = flow {
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
            else -> emit(Result.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }
}