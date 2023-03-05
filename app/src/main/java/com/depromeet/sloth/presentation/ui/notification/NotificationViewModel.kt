package com.depromeet.sloth.presentation.ui.notification

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.notification.NotificationListResponse
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.usecase.notification.FetchNotificationListUseCase
import com.depromeet.sloth.domain.usecase.notification.UpdateNotificationStateUseCase
import com.depromeet.sloth.presentation.ui.base.BaseViewModel
import com.depromeet.sloth.util.INTERNET_CONNECTION_ERROR
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _fetchLessonListSuccessEvent = MutableSharedFlow<List<NotificationListResponse>>()
    val fetchLessonListSuccessEvent: SharedFlow<List<NotificationListResponse>> = _fetchLessonListSuccessEvent.asSharedFlow()

    fun fetchNotificationList() = viewModelScope.launch {
        fetchNotificationListUseCase(0, 99999)
            .onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        setInternetError(false)
                        _fetchLessonListSuccessEvent.emit(result.data)
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
            }
    }

    fun updateNotificationState(alarmId: Long, isStateChanged: () -> Unit) {
        viewModelScope.launch {
            updateNotificationStateUseCase(alarmId)
                .onEach { result ->
                    setLoading(result is Result.Loading)
                }.collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> {
                            setInternetError(false)
                            isStateChanged.invoke()
                        }
                        is Result.Error -> {
                            when {
                                result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                    setInternetError(true)
                                }
                                result.statusCode == UNAUTHORIZED -> {
                                    navigateToExpireDialog()
                                }
                                else -> showToast(stringResourcesProvider.getString(R.string.notification_list_update_fail))
                            }
                        }
                    }
                }
        }
    }

    override fun retry() = Unit
}