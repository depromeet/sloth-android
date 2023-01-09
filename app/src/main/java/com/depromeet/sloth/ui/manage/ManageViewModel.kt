package com.depromeet.sloth.ui.manage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.request.member.MemberUpdateRequest
import com.depromeet.sloth.data.model.request.notification.NotificationUpdateRequest
import com.depromeet.sloth.data.model.response.member.MemberResponse
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.use_case.member.GetMemberInfoUseCase
import com.depromeet.sloth.domain.use_case.member.LogOutUseCase
import com.depromeet.sloth.domain.use_case.member.RemoveAuthTokenUseCase
import com.depromeet.sloth.domain.use_case.member.UpdateMemberInfoUseCase
import com.depromeet.sloth.domain.use_case.notification.UpdateNotificationStatusUseCase
import com.depromeet.sloth.extensions.getMutableStateFlow
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.item.Member
import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
import com.depromeet.sloth.util.INTERNET_CONNECTION_ERROR
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


//TODO 데이터의 단일화
@HiltViewModel
class ManageViewModel @Inject constructor(
    private val getMemberInfoUseCase: GetMemberInfoUseCase,
    private val updateMemberInfoUseCase: UpdateMemberInfoUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val removeAuthTokenUseCase: RemoveAuthTokenUseCase,
    private val updateNotificationStatusUseCase: UpdateNotificationStatusUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    private val _updateMemberSuccess = MutableSharedFlow<Unit>()
    val updateMemberSuccess: SharedFlow<Unit> = _updateMemberSuccess.asSharedFlow()

    private val _logoutSuccess = MutableSharedFlow<Unit>()
    val logoutSuccess: SharedFlow<Unit> = _logoutSuccess.asSharedFlow()

    private val _memberName =
        savedStateHandle.getMutableStateFlow(KEY_MEMBER_NAME, DEFAULT_STRING_VALUE)
    val memberName: StateFlow<String> = _memberName.asStateFlow()

    private val _previousMemberName =
        savedStateHandle.getMutableStateFlow(KEY_PREVIOUS_MEMBER_NAME, DEFAULT_STRING_VALUE)
    val previousMemberName: StateFlow<String> = _previousMemberName.asStateFlow()

    private val _member = MutableStateFlow(Member())
    val member: StateFlow<Member> = _member.asStateFlow()

//    private val _member = MutableStateFlow(MemberUiState())
//    val member: StateFlow<MemberUiState> = _member.asStateFlow()

    private val _memberNotificationReceive =
        savedStateHandle.getMutableStateFlow(KEY_MEMBER_NOTIFICATION_RECEIVE, false)
    val memberNotificationReceive: StateFlow<Boolean> = _memberNotificationReceive.asStateFlow()

    private val _navigateToUpdateProfileDialogEvent = MutableSharedFlow<Unit>()
    val navigateToUpdateProfileDialogEvent: SharedFlow<Unit> =
        _navigateToUpdateProfileDialogEvent.asSharedFlow()

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

    private val _updateMemberValidation = MutableStateFlow(false)
    val updateMemberValidation: StateFlow<Boolean> = _updateMemberValidation.asStateFlow()

    fun fetchMemberInfo() = viewModelScope.launch {
        getMemberInfoUseCase()
            .onEach { result ->
                showLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        internetError(false)
                        setMemberInfo(result.data)
                    }
                    is Result.Error -> {
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            internetError(true)
                        }
                        else if (result.statusCode == UNAUTHORIZED) {
                            showForbiddenDialogEvent()
                        }
                        else {
                            showToastEvent(stringResourcesProvider.getString(R.string.member_fetch_fail))
                        }
                    }
                }
            }
    }

    fun updateMemberInfo() = viewModelScope.launch {
        updateMemberInfoUseCase(MemberUpdateRequest(memberName.value))
            .onEach {result ->
                showLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        _updateMemberSuccess.emit(Unit)
                        showToastEvent(stringResourcesProvider.getString(R.string.member_update_success))
                        setPreviousMemberName(result.data.memberName)
                        // btnMemberName 활성 상태 초기화
                        setUpdateMemberValidation(false)
                    }

                    is Result.Error -> {
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            showToastEvent(stringResourcesProvider.getString(R.string.member_update_fail_by_internet_error))
                        } else if (result.statusCode == UNAUTHORIZED) {
                            showForbiddenDialogEvent()
                        } else {
                            showToastEvent(stringResourcesProvider.getString(R.string.member_update_fail))
                        }
                    }
                }
            }
    }

    fun updateNotificationSwitch(check: Boolean) {
        if (memberNotificationReceive.value != check) {
            viewModelScope.launch {
                updateNotificationStatusUseCase(NotificationUpdateRequest(check))
                    .onEach {
                        showLoading(it is Result.Loading)
                    }.collect { result ->
                        when (result) {
                            is Result.Loading -> return@collect
                            is Result.Success -> {
                                showToastEvent(stringResourcesProvider.getString(R.string.noti_update_complete))
                                setMemberNotificationReceive(check)
                            }

                            is Result.Error -> {
                                if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                                    showToastEvent(stringResourcesProvider.getString(R.string.noti_update_fail_by_internet_error))
                                } else if (result.statusCode == UNAUTHORIZED) {
                                    showForbiddenDialogEvent()
                                } else {
                                    showToastEvent(stringResourcesProvider.getString(R.string.noti_update_fail))
                                }
                            }
                        }
                    }
            }
        }
    }

    fun logout() = viewModelScope.launch {
        logOutUseCase()
            .onEach {result ->
                showLoading(result is Result.Loading)
            }.collect {result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        _logoutSuccess.emit(Unit)
                    }

                    is Result.Error -> {
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            Timber.d(INTERNET_CONNECTION_ERROR)
                            showToastEvent(stringResourcesProvider.getString(R.string.logout_fail_by_internet_error))
                        } else if (result.statusCode == UNAUTHORIZED) {
                            showForbiddenDialogEvent()
                        } else {
                            showToastEvent(stringResourcesProvider.getString(R.string.logout_fail))
                        }
                    }
                }
            }
    }

    private fun setMemberInfo(memberResponse: MemberResponse) {
        _member.value = Member(
            email = memberResponse.email,
            memberName = memberResponse.memberName,
            isEmailProvided = memberResponse.isEmailProvided,
            isPushAlarmUse = memberResponse.isPushAlarmUse
        )
        setMemberName(memberResponse.memberName)
        setPreviousMemberName(memberResponse.memberName)
        setMemberNotificationReceive(memberResponse.isPushAlarmUse)
    }

    fun setMemberName(memberName: String) {
        _memberName.value = memberName
    }

    private fun setPreviousMemberName(previousMemberName: String) {
        _previousMemberName.value = previousMemberName
    }

    private fun setMemberNotificationReceive(check: Boolean) {
        _memberNotificationReceive.value = check
    }

    fun setUpdateMemberValidation(isEnable: Boolean) {
        _updateMemberValidation.value = isEnable
    }

    fun navigateToUpdateProfileDialog() = viewModelScope.launch {
        _navigateToUpdateProfileDialogEvent.emit(Unit)
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

    fun removeAuthToken() = viewModelScope.launch {
        removeAuthTokenUseCase()
    }

    override fun retry() {
        fetchMemberInfo()
    }

//    data class MemberUiState(
//        val email: String = "",
//        val memberName: String = "",
//        val previousMemberName: String = "",
//        val isEmailProvided: Boolean = false,
//        val isPushAlarmUse: Boolean = false,
//    )

    companion object {
        private const val KEY_MEMBER_NAME = "memberName"
        private const val KEY_PREVIOUS_MEMBER_NAME = "previousMemberName"
        private const val KEY_MEMBER_NOTIFICATION_RECEIVE = "notificationReceive"
    }
}