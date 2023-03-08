package com.depromeet.sloth.presentation.ui.manage

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.request.notification.NotificationUpdateRequest
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.usecase.lesson.FetchLessonStatisticsInfoUseCase
import com.depromeet.sloth.domain.usecase.member.DeleteAuthTokenUseCase
import com.depromeet.sloth.domain.usecase.member.FetchMemberInfoUseCase
import com.depromeet.sloth.domain.usecase.member.LogOutUseCase
import com.depromeet.sloth.domain.usecase.notification.UpdateNotificationStatusUseCase
import com.depromeet.sloth.presentation.ui.base.BaseViewModel
import com.depromeet.sloth.util.INTERNET_CONNECTION_ERROR
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ManageViewModel @Inject constructor(
    private val fetchMemberInfoUseCase: FetchMemberInfoUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val deleteAuthTokenUseCase: DeleteAuthTokenUseCase,
    private val updateNotificationStatusUseCase: UpdateNotificationStatusUseCase,
    private val fetchLessonStatisticsInfoUseCase: FetchLessonStatisticsInfoUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
) : BaseViewModel() {

    private var fetchLessonStatisticsInfoJob: Job? = null
    private var updateNotificationStatusJob: Job? = null

    private val _uiState = MutableStateFlow(MemberUiState())
    val uiState: StateFlow<MemberUiState> = _uiState.asStateFlow()

    private val _navigateToUpdateMemberDialogEvent = MutableSharedFlow<Unit>()
    val navigateToUpdateMemberDialogEvent: SharedFlow<Unit> =
        _navigateToUpdateMemberDialogEvent.asSharedFlow()

    private val _navigateToSettingEvent = MutableSharedFlow<Unit>()
    val navigateToSettingEvent: SharedFlow<Unit> = _navigateToSettingEvent.asSharedFlow()

    private val _navigateToContactEvent = MutableSharedFlow<Unit>()
    val navigateToContactEvent: SharedFlow<Unit> = _navigateToContactEvent.asSharedFlow()

    private val _navigateToPrivatePolicyEvent = MutableSharedFlow<Unit>()
    val navigateToPrivatePolicyEvent: SharedFlow<Unit> =
        _navigateToPrivatePolicyEvent.asSharedFlow()

    private val _navigateToLogoutDialogEvent = MutableSharedFlow<Unit>()
    val navigateToLogoutDialogEvent: SharedFlow<Unit> = _navigateToLogoutDialogEvent.asSharedFlow()

    private val _navigateToWithdrawalDialogEvent = MutableSharedFlow<Unit>()
    val navigateToWithdrawalDialogEvent: SharedFlow<Unit> =
        _navigateToWithdrawalDialogEvent.asSharedFlow()

    private val _logoutSuccessEvent = MutableSharedFlow<Unit>()
    val logoutSuccessEvent: SharedFlow<Unit> = _logoutSuccessEvent.asSharedFlow()

    private val _logoutCancelEvent = MutableSharedFlow<Unit>()
    val logoutCancelEvent: SharedFlow<Unit> = _logoutCancelEvent.asSharedFlow()

    init {
        fetchMemberInfo()
    }

    private fun fetchMemberInfo() {
        viewModelScope.launch {
            fetchMemberInfoUseCase()
                .onEach { result ->
                    setLoading(result is Result.Loading)
                }.collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> {
                            setInternetError(false)
                            _uiState.update { memberUiState ->
                                memberUiState.copy(
                                    email = result.data.email,
                                    memberName = result.data.memberName,
                                    isEmailProvided = result.data.isEmailProvided,
                                    isPushAlarmUse = result.data.isPushAlarmUse
                                )
                            }
                        }
                        is Result.Error -> {
                            when {
                                result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                    setInternetError(true)
                                }
                                result.statusCode == UNAUTHORIZED -> {
                                    navigateToExpireDialog()
                                }
                                else -> showToast(stringResourcesProvider.getString(R.string.member_fetch_fail))
                            }
                        }
                    }
                }
        }
    }

    fun fetchLessonStatisticsInfo() {
        if (fetchLessonStatisticsInfoJob != null) return

        fetchLessonStatisticsInfoJob = viewModelScope.launch {
            fetchLessonStatisticsInfoUseCase()
                .onEach { result ->
                    setLoading(result is Result.Loading)
                }.collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> {
                            setInternetError(false)
                            _uiState.update { memberUiState ->
                                memberUiState.copy(
                                    currentProgressRate = (result.data.finishedLessonsPrice * 100 / result.data.expiredLessonsPrice).toFloat(),
                                    expiredLessonsCnt = result.data.expiredLessonsCnt,
                                    expiredLessonsPrice = result.data.expiredLessonsPrice,
                                    finishedLessonsCnt = result.data.finishedLessonsCnt,
                                    finishedLessonsPrice = result.data.finishedLessonsPrice,
                                    notFinishedLessonsCnt = result.data.notFinishedLessonsCnt,
                                    notFinishedLessonsPrice = result.data.notFinishedLessonsPrice
                                )
                            }
                        }
                        is Result.Error -> {
                            when {
                                result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                    setInternetError(true)
                                }
                                result.statusCode == UNAUTHORIZED -> {
                                    navigateToExpireDialog()
                                }
                                else -> showToast(stringResourcesProvider.getString(R.string.member_fetch_fail))
                            }
                        }
                    }
                    fetchLessonStatisticsInfoJob = null
                }
        }
    }

    fun updateNotificationSwitch(check: Boolean) {
        if (updateNotificationStatusJob != null) return

        if (uiState.value.isPushAlarmUse != check) {
            viewModelScope.launch {
                updateNotificationStatusUseCase(NotificationUpdateRequest(check))
                    .onEach {
                        setLoading(it is Result.Loading)
                    }.collect { result ->
                        when (result) {
                            is Result.Loading -> return@collect
                            is Result.Success -> {
                                showToast(stringResourcesProvider.getString(R.string.noti_update_complete))
                                _uiState.update { memberUiState ->
                                    memberUiState.copy(
                                        isPushAlarmUse = check
                                    )
                                }
                            }
                            is Result.Error -> {
                                when {
                                    result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                        showToast(stringResourcesProvider.getString(R.string.noti_update_fail_by_internet_error))
                                    }
                                    result.statusCode == UNAUTHORIZED -> {
                                        navigateToExpireDialog()
                                    }
                                    else -> showToast(stringResourcesProvider.getString(R.string.noti_update_fail))
                                }
                            }
                        }
                        updateNotificationStatusJob = null
                    }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logOutUseCase()
                .onEach { result ->
                    setLoading(result is Result.Loading)
                }.collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> {
                            showToast(stringResourcesProvider.getString(R.string.logout_complete))
                            deleteAuthToken()
                            _logoutSuccessEvent.emit(Unit)
                        }

                        is Result.Error -> {
                            when {
                                result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                    showToast(stringResourcesProvider.getString(R.string.logout_fail_by_internet_error))
                                }
                                result.statusCode == UNAUTHORIZED -> {
                                    navigateToExpireDialog()
                                }
                                else -> showToast(stringResourcesProvider.getString(R.string.logout_fail))
                            }
                        }
                    }
                }
        }
    }

    fun navigateToUpdateProfileDialog() = viewModelScope.launch {
        _navigateToUpdateMemberDialogEvent.emit(Unit)
    }

    fun navigateToSetting() = viewModelScope.launch {
        _navigateToSettingEvent.emit(Unit)
    }

    fun navigateToPrivacyPolicy() = viewModelScope.launch {
        _navigateToPrivatePolicyEvent.emit(Unit)
    }

    fun navigateToContact() = viewModelScope.launch {
        _navigateToContactEvent.emit(Unit)
    }

    fun navigateToLogoutDialog() = viewModelScope.launch {
        _navigateToLogoutDialogEvent.emit(Unit)
    }

    fun navigateToWithdrawalDialog() = viewModelScope.launch {
        _navigateToWithdrawalDialogEvent.emit(Unit)
    }

    fun navigateToManage() = viewModelScope.launch {
        _logoutCancelEvent.emit(Unit)
    }

    private fun deleteAuthToken() = viewModelScope.launch {
        deleteAuthTokenUseCase()
    }

    override fun retry() {
        fetchLessonStatisticsInfo()
    }

    data class MemberUiState(
        val email: String = "",
        val memberName: String = "",
        val isEmailProvided: Boolean = false,
        val isPushAlarmUse: Boolean = false,
        val currentProgressRate: Float = 0f,
        val expiredLessonsCnt: Int = 0,
        val expiredLessonsPrice: Int = 0,
        val finishedLessonsCnt: Int = 0,
        val finishedLessonsPrice: Int = 0,
        val notFinishedLessonsCnt: Int = 0,
        val notFinishedLessonsPrice: Int = 0
    )
}