package com.depromeet.data.repository

import android.content.Context
import android.provider.Settings
import com.depromeet.data.mapper.toEntity
import com.depromeet.data.mapper.toModel
import com.depromeet.data.model.request.notification.NotificationRegisterRequest
import com.depromeet.data.model.response.notification.NotificationFetchResponse
import com.depromeet.data.network.service.NotificationService
import com.depromeet.data.preferences.PreferenceManager
import com.depromeet.data.util.DEFAULT_STRING_VALUE
import com.depromeet.data.util.INTERNET_CONNECTION_ERROR
import com.depromeet.data.util.KEY_AUTHORIZATION
import com.depromeet.domain.entity.NotificationEntity
import com.depromeet.domain.entity.NotificationUpdateRequestEntity
import com.depromeet.domain.repository.NotificationRepository
import com.depromeet.domain.util.Result
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

    override fun registerNotificationToken(fcmToken: String) = flow {
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


    override fun updateNotificationStatus(notificationUpdateRequestEntity: NotificationUpdateRequestEntity) = flow {
        emit(Result.Loading)
        val response = notificationService.updateFCMTokenUse(notificationUpdateRequestEntity.toModel()) ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: DEFAULT_STRING_VALUE))
            }
            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
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
                emit(Result.Success(response.body()?.toEntity() ?: NotificationFetchResponse.EMPTY.toEntity()))
            }
            else -> emit(Result.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }


    override fun fetchNotificationList(page: Int, size: Int) = flow {
        emit(Result.Loading)
        val response = notificationService.fetchNotificationList(page, size) ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body()?.toEntity() ?: emptyList<NotificationEntity>()))
            }
            else -> emit(Result.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }


    /*
    // TODO 기존의 방법들 처럼 Result 로 값을 래핑할 필요는 없는건가? paging 내부에서 네트워크 에러를 포함한 에러 핸들링을 수행 해주므로..?
    override fun fetchNotificationList(page: Int, size: Int): Flow<PagingData<NotificationListResponse>> {
        val pagingSourceFactory = { NotificationPagingSource(notificationService) }

        return Pager(
            config = PagingConfig(
                // 어떤 기기로 동작 시키든 뷰홀더에 표시할 데이터가 모자르지 않을 정도의 값으로 설정
                pageSize = PAGING_SIZE,
                // true -> repository 의 전체 데이터 사이즈를 받아와서 recyclerview 의 placeholder 를 미리 만들어 놓음
                // 화면에 표시 되지 않는 항목은 null로 표시
                // 필요할 때 필요한 만큼만 로딩 하려면 false
                enablePlaceholders = false,
                // 페이저가 메모리에 가지고 있을 수 있는 최대 개수, 페이지 사이즈의 2~3배 정도
                maxSize = PAGING_SIZE * 3
            ),
            // api 호출 결과를 팩토리에 전달
            pagingSourceFactory = pagingSourceFactory
        ).flow // 결과를 flow 로 변환
    }
     */

    override fun updateNotificationState(alarmId: Long) = flow {
        emit(Result.Loading)
        val response = notificationService.updateNotificationState(alarmId) ?: run {
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
            when (throwable) {
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
}