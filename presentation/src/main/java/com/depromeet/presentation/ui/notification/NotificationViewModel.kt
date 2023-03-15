package com.depromeet.presentation.ui.notification

import androidx.lifecycle.viewModelScope
import com.depromeet.domain.usecase.notification.FetchNotificationListUseCase
import com.depromeet.domain.usecase.notification.UpdateNotificationStateUseCase
import com.depromeet.domain.util.Result
import com.depromeet.presentation.R
import com.depromeet.presentation.di.StringResourcesProvider
import com.depromeet.presentation.mapper.toUiModel
import com.depromeet.presentation.model.Notification
import com.depromeet.presentation.ui.base.BaseViewModel
import com.depromeet.presentation.util.INTERNET_CONNECTION_ERROR
import com.depromeet.presentation.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val fetchNotificationListUseCase: FetchNotificationListUseCase,
    private val updateNotificationStateUseCase: UpdateNotificationStateUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
) : BaseViewModel() {

    private var fetchNotificationListJob: Job? = null
    private var updateNotificationStatusJob: Job? = null

    private val _fetchLessonListSuccessEvent = MutableSharedFlow<List<Notification>>()
    val fetchLessonListSuccessEvent: SharedFlow<List<Notification>> = _fetchLessonListSuccessEvent.asSharedFlow()

    /*
    val notificationList: Flow<PagingData<NotificationListResponse>> =
        fetchNotificationListUseCase(STARTING_PAGE_INDEX, PAGING_SIZE)
            .cachedIn(viewModelScope)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                PagingData.empty()
            )
     */

    fun fetchNotificationList() {
        if (fetchNotificationListJob != null) return

        fetchNotificationListJob = viewModelScope.launch {
            fetchNotificationListUseCase(0, 9999)
                .onEach { result ->
                    setLoading(result is Result.Loading)
                }.collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> {
                            setInternetError(false)
                            _fetchLessonListSuccessEvent.emit(result.data.toUiModel())
                        }
                        is Result.Error -> {
                            when {
                                result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                    setInternetError(true)
                                }
                                result.statusCode == UNAUTHORIZED -> {
                                    navigateToExpireDialog()
                                }
                                else -> showToast(stringResourcesProvider.getString(R.string.notification_list_fetch_fail))
                            }
                        }
                    }
                    fetchNotificationListJob = null
                }
        }
    }

    fun updateNotificationState(notification: Notification, isStateChanged: () -> Unit) {
        // 이미 읽은 알림일 경우 api 를 호출 하지 않음
        if (notification.readTime != null) return

        if (updateNotificationStatusJob != null) return

        updateNotificationStatusJob = viewModelScope.launch {
            updateNotificationStateUseCase(notification.alarmId)
                .onEach { result ->
                    setLoading(result is Result.Loading)
                }.collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> {
                            isStateChanged.invoke()
                        }
                        is Result.Error -> {
                            when {
                                result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                    showToast(stringResourcesProvider.getString(R.string.notification_list_update_fail))
                                }
                                result.statusCode == UNAUTHORIZED -> {
                                    navigateToExpireDialog()
                                }
                                else -> showToast(stringResourcesProvider.getString(R.string.notification_list_update_fail))
                            }
                        }
                    }
                    updateNotificationStatusJob = null
                }
        }
    }

    override fun retry() = fetchNotificationList()
}