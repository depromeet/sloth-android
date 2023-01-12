package com.depromeet.sloth.presentation.manage

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.request.notification.NotificationUpdateRequest
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.use_case.member.DeleteAuthTokenUseCase
import com.depromeet.sloth.domain.use_case.member.FetchMemberInfoUseCase
import com.depromeet.sloth.domain.use_case.member.LogOutUseCase
import com.depromeet.sloth.domain.use_case.notification.UpdateNotificationStatusUseCase
import com.depromeet.sloth.presentation.base.BaseViewModel
import com.depromeet.sloth.util.INTERNET_CONNECTION_ERROR
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class ManageViewModel @Inject constructor(
    private val fetchMemberInfoUseCase: FetchMemberInfoUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val deleteAuthTokenUseCase: DeleteAuthTokenUseCase,
    private val updateNotificationStatusUseCase: UpdateNotificationStatusUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(MemberUiState())
    val uiState: StateFlow<MemberUiState> = _uiState.asStateFlow()

    private val _navigateToUpdateMemberDialogEvent = MutableSharedFlow<Unit>()
    val navigateToUpdateMemberDialogEvent: SharedFlow<Unit> =
        _navigateToUpdateMemberDialogEvent.asSharedFlow()

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

    fun fetchMemberInfo() = viewModelScope.launch {
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
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            setInternetError(true)
                        } else if (result.statusCode == UNAUTHORIZED) {
                            navigateToExpireDialog()
                        } else {
                            showToast(stringResourcesProvider.getString(R.string.member_fetch_fail))
                        }
                    }
                }
            }
    }

    fun updateNotificationSwitch(check: Boolean) {
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
                                if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                                    showToast(stringResourcesProvider.getString(R.string.noti_update_fail_by_internet_error))
                                } else if (result.statusCode == UNAUTHORIZED) {
                                    navigateToExpireDialog()
                                } else {
                                    showToast(stringResourcesProvider.getString(R.string.noti_update_fail))
                                }
                            }
                        }
                    }
            }
        }
    }

    fun logout() = viewModelScope.launch {
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
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            Timber.d(INTERNET_CONNECTION_ERROR)
                            showToast(stringResourcesProvider.getString(R.string.logout_fail_by_internet_error))
                        } else if (result.statusCode == UNAUTHORIZED) {
                            navigateToExpireDialog()
                        } else {
                            showToast(stringResourcesProvider.getString(R.string.logout_fail))
                        }
                    }
                }
            }
    }

    fun navigateToUpdateProfileDialog() = viewModelScope.launch {
        _navigateToUpdateMemberDialogEvent.emit(Unit)
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

    fun deleteAuthToken() = viewModelScope.launch {
        deleteAuthTokenUseCase()
    }

    override fun retry() {
        fetchMemberInfo()
    }

    data class MemberUiState(
        val email: String = "",
        val memberName: String = "",
        val isEmailProvided: Boolean = false,
        val isPushAlarmUse: Boolean = false,
    )
}