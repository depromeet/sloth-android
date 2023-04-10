package com.depromeet.data.source.remote

import android.content.Context
import android.provider.Settings
import com.depromeet.data.mapper.toEntity
import com.depromeet.data.mapper.toModel
import com.depromeet.data.model.request.notification.NotificationRegisterRequest
import com.depromeet.data.model.response.notification.NotificationFetchResponse
import com.depromeet.data.source.local.preferences.PreferenceManager
import com.depromeet.data.source.remote.service.NotificationService
import com.depromeet.data.util.DEFAULT_STRING_VALUE
import com.depromeet.data.util.RESPONSE_NULL_ERROR
import com.depromeet.data.util.handleExceptions
import com.depromeet.data.util.handleResponse
import com.depromeet.domain.entity.NotificationEntity
import com.depromeet.domain.entity.NotificationUpdateRequestEntity
import com.depromeet.domain.util.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NotificationRemoteDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationService: NotificationService,
    private val preferences: PreferenceManager
) : NotificationRemoteDataSource {

    private val deviceId: String by lazy {
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    override fun registerNotificationToken(fcmToken: String) = flow {
        emit(Result.Loading)
        val response = notificationService.registerFCMToken(NotificationRegisterRequest(deviceId, fcmToken)) ?: run {
            emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
            return@flow
        }
        emit(response.handleResponse(preferences) {
            it.body() ?: DEFAULT_STRING_VALUE
        })
    }.handleExceptions()


    override fun updateNotificationReceiveStatus(notificationUpdateRequestEntity: NotificationUpdateRequestEntity) = flow {
        emit(Result.Loading)
        val response = notificationService.updateFCMTokenUse(notificationUpdateRequestEntity.toModel()) ?: run {
            emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
            return@flow
        }
        emit(response.handleResponse(preferences) {
            it.body() ?: DEFAULT_STRING_VALUE
        })
    }.handleExceptions()

    override fun fetchNotificationToken(deviceId: String) = flow {
        emit(Result.Loading)
        val response = notificationService.fetchFCMToken(deviceId) ?: run {
            emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
            return@flow
        }
        emit(response.handleResponse(preferences) {
            it.body()?.toEntity() ?: NotificationFetchResponse.EMPTY.toEntity()
        })
    }.handleExceptions()


    override fun fetchNotificationList(page: Int, size: Int) = flow {
        emit(Result.Loading)
        val response = notificationService.fetchNotificationList(page, size) ?: run {
            emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
            return@flow
        }
        emit(response.handleResponse(preferences) {
            it.body()?.toEntity() ?: emptyList<NotificationEntity>()
        })
    }.handleExceptions()


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

    override fun updateNotificationReadStatus(alarmId: Long) = flow {
        emit(Result.Loading)
        val response = notificationService.updateNotificationState(alarmId) ?: run {
            emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
            return@flow
        }
        emit(response.handleResponse(preferences) {
            it.body() ?: DEFAULT_STRING_VALUE
        })
    }.handleExceptions()
}