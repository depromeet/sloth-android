package com.depromeet.presentation.ui.manage

import androidx.lifecycle.viewModelScope
import com.depromeet.domain.usecase.lesson.FetchLessonStatisticsInfoUseCase
import com.depromeet.domain.usecase.userauth.DeleteAuthTokenUseCase
import com.depromeet.domain.usecase.userprofile.FetchUserProfileUseCase
import com.depromeet.domain.usecase.userauth.LogoutUseCase
import com.depromeet.domain.usecase.notification.UpdateNotificationReceiveStatusUseCase
import com.depromeet.domain.usecase.userauth.WithdrawUseCase
import com.depromeet.domain.util.Result
import com.depromeet.presentation.R
import com.depromeet.presentation.di.StringResourcesProvider
import com.depromeet.presentation.mapper.toEntity
import com.depromeet.presentation.model.NotificationUpdateRequest
import com.depromeet.presentation.ui.base.BaseViewModel
import com.depromeet.presentation.util.DEFAULT_STRING_VALUE
import com.depromeet.presentation.util.INTERNET_CONNECTION_ERROR
import com.depromeet.presentation.util.SERVER_CONNECTION_ERROR
import com.depromeet.presentation.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


data class UserProfileUiState(
    val email: String = "",
    val picture: String? = "",
    val userName: String = "",
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

@HiltViewModel
class ManageViewModel @Inject constructor(
    private val fetchUserProfileUseCase: FetchUserProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val withdrawUseCase: WithdrawUseCase,
    private val deleteAuthTokenUseCase: DeleteAuthTokenUseCase,
    private val updateNotificationReceiveStatusUseCase: UpdateNotificationReceiveStatusUseCase,
    private val fetchLessonStatisticsInfoUseCase: FetchLessonStatisticsInfoUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
) : BaseViewModel() {

    private var fetchLessonStatisticsInfoJob: Job? = null
    private var updateNotificationStatusJob: Job? = null

    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    private val _navigateToUpdateUserProfileDialogEvent = MutableSharedFlow<Unit>()
    val navigateToUpdateUserProfileDialogEvent: SharedFlow<Unit> =
        _navigateToUpdateUserProfileDialogEvent.asSharedFlow()

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

    private val _withdrawSuccessEvent = MutableSharedFlow<Unit>()
    val withdrawSuccessEvent: SharedFlow<Unit> = _withdrawSuccessEvent.asSharedFlow()

    private val _withdrawCancelEvent = MutableSharedFlow<Unit>()
    val withdrawCancelEvent: SharedFlow<Unit> = _withdrawCancelEvent.asSharedFlow()

    private val _navigateToUserProfileDetailEvent = MutableSharedFlow<String>()
    val navigateToUserProfileDetailEvent: SharedFlow<String> = _navigateToUserProfileDetailEvent.asSharedFlow()

    fun fetchUserProfile() {
        viewModelScope.launch {
            fetchUserProfileUseCase()
                .onEach { result ->
                    setLoading(result is Result.Loading)
                }.collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> {
                            setInternetError(false)
                            _uiState.update { userProfileUiState ->
                                userProfileUiState.copy(
                                    email = result.data.email,
                                    picture = result.data.picture,
                                    userName = result.data.userName,
                                    isEmailProvided = result.data.isEmailProvided,
                                    isPushAlarmUse = result.data.isPushAlarmUse
                                )
                            }
                        }

                        is Result.Error -> {
                            when {
                                result.throwable.message == SERVER_CONNECTION_ERROR -> {
                                    showToast(stringResourcesProvider.getString(R.string.user_profile_fetch_fail_by_server_error))
                                }

                                result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                    setInternetError(true)
                                }

                                result.statusCode == UNAUTHORIZED -> {
                                    navigateToExpireDialog()
                                }

                                else -> showToast(stringResourcesProvider.getString(R.string.user_profile_fetch_fail))
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
                            _uiState.update { userProfileUiState ->
                                userProfileUiState.copy(
                                    currentProgressRate = if (result.data.expiredLessonsPrice == 0) 0f
                                    else (result.data.finishedLessonsPrice * 100 / result.data.expiredLessonsPrice).toFloat(),
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
                                result.throwable.message == SERVER_CONNECTION_ERROR -> {
                                    showToast(stringResourcesProvider.getString(R.string.statistics_fetch_fail_by_server_error))
                                }

                                result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                    setInternetError(true)
                                }

                                result.statusCode == UNAUTHORIZED -> {
                                    navigateToExpireDialog()
                                }

                                else -> showToast(stringResourcesProvider.getString(R.string.statisticsInfo_fetch_fail))
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
                updateNotificationReceiveStatusUseCase(NotificationUpdateRequest(check).toEntity())
                    .onEach {
                        setLoading(it is Result.Loading)
                    }.collect { result ->
                        when (result) {
                            is Result.Loading -> return@collect
                            is Result.Success -> {
                                showToast(stringResourcesProvider.getString(R.string.noti_update_complete))
                                _uiState.update { userProfileUiState ->
                                    userProfileUiState.copy(
                                        isPushAlarmUse = check
                                    )
                                }
                            }

                            is Result.Error -> {
                                when {
                                    result.throwable.message == SERVER_CONNECTION_ERROR -> {
                                        showToast(stringResourcesProvider.getString(R.string.noti_update_fail_by_server_error))
                                    }

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
            logoutUseCase()
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
                            Timber.d(result.throwable)
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

    fun withdraw() {
        viewModelScope.launch {
            withdrawUseCase()
                .onEach { result ->
                    setLoading(result is Result.Loading)
                }.collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> {
                            showToast(stringResourcesProvider.getString(R.string.withdraw_complete))
                            deleteAuthToken()
                            _withdrawSuccessEvent.emit(Unit)
                        }

                        is Result.Error -> {
                            Timber.d(result.throwable)
                            when {
                                result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                    showToast(stringResourcesProvider.getString(R.string.withdraw_fail_by_internet_error))
                                }

                                result.statusCode == UNAUTHORIZED -> {
                                    navigateToExpireDialog()
                                }

                                else -> showToast(stringResourcesProvider.getString(R.string.withdraw_fail))
                            }
                        }
                    }
                }
        }
    }

    fun navigateToUpdateProfileDialog() = viewModelScope.launch {
        _navigateToUpdateUserProfileDialogEvent.emit(Unit)
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

    fun cancelLogout() = viewModelScope.launch {
        _logoutCancelEvent.emit(Unit)
    }

    fun cancelWithdraw() = viewModelScope.launch {
        _withdrawCancelEvent.emit(Unit)
    }

    private fun deleteAuthToken() = viewModelScope.launch {
        deleteAuthTokenUseCase()
    }

    fun navigateToUserProfileDetail() {
        if (uiState.value.picture == null) return

        viewModelScope.launch {
            _navigateToUserProfileDetailEvent.emit(uiState.value.picture!!)
        }
    }

    override fun retry() {
        fetchLessonStatisticsInfo()
    }
}