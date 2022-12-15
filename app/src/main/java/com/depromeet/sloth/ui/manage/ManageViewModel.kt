package com.depromeet.sloth.ui.manage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.data.model.request.member.MemberUpdateRequest
import com.depromeet.sloth.data.model.request.notification.NotificationUpdateRequest
import com.depromeet.sloth.data.model.response.member.MemberResponse
import com.depromeet.sloth.data.model.response.member.MemberUpdateResponse
import com.depromeet.sloth.domain.use_case.member.GetMemberInfoUseCase
import com.depromeet.sloth.domain.use_case.member.LogOutUseCase
import com.depromeet.sloth.domain.use_case.member.RemoveAuthTokenUseCase
import com.depromeet.sloth.domain.use_case.member.UpdateMemberInfoUseCase
import com.depromeet.sloth.domain.use_case.notification.UpdateNotificationStatusUseCase
import com.depromeet.sloth.extensions.getMutableStateFlow
import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ManageViewModel @Inject constructor(
    getMemberInfoUseCase: GetMemberInfoUseCase,
    private val updateMemberInfoUseCase: UpdateMemberInfoUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val removeAuthTokenUseCase: RemoveAuthTokenUseCase,
    private val updateNotificationStatusUseCase: UpdateNotificationStatusUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val fetchMemberInfoEvent: Flow<Result<MemberResponse>> = getMemberInfoUseCase()

//    val fetchMemberInfoEvent: Flow<Result<MemberResponse>> =
//        getMemberInfoUseCase().asResult()

    private val _updateMemberInfoEvent = MutableSharedFlow<Result<MemberUpdateResponse>>()
    val updateMemberInfoEvent: SharedFlow<Result<MemberUpdateResponse>> =
        _updateMemberInfoEvent.asSharedFlow()

    private val _updateToReceiveNotificationEvent = MutableSharedFlow<Result<String>>()
    val updateToReceiveNotificationEvent: SharedFlow<Result<String>> =
        _updateToReceiveNotificationEvent.asSharedFlow()

    // api response 변경 후
//    private val _notificationReceiveState = MutableSharedFlow<Result<NotificationUpdateResponse>>()
//    val notificationReceiveState: SharedFlow<Result<NotificationUpdateResponse>> =
//        _notificationReceiveState.asSharedFlow()

    private val _logoutEvent = MutableSharedFlow<Result<String>>()
    val logoutEvent: SharedFlow<Result<String>> = _logoutEvent.asSharedFlow()

    private val _member = MutableStateFlow(MemberResponse())
    val member: StateFlow<MemberResponse> = _member.asStateFlow()

    private val _memberName =
        savedStateHandle.getMutableStateFlow(KEY_MEMBER_NAME, DEFAULT_STRING_VALUE)
    val memberName: StateFlow<String> = _memberName.asStateFlow()

    private val _memberNotificationReceive =
        savedStateHandle.getMutableStateFlow(KEY_MEMBER_NOTIFICATION_RECEIVE, false)
    val memberNotificationReceive: StateFlow<Boolean> = _memberNotificationReceive.asStateFlow()

    private val _showUpdateProfileDialogEvent = MutableSharedFlow<Unit>()
    val showUpdateProfileDialogEvent: SharedFlow<Unit> = _showUpdateProfileDialogEvent.asSharedFlow()

    private val _navigateToContactEvent = MutableSharedFlow<Unit>()
    val navigateToContactEvent: SharedFlow<Unit> = _navigateToContactEvent.asSharedFlow()

    private val _navigateToPrivatePolicyEvent = MutableSharedFlow<Unit>()
    val navigateToPrivatePolicyEvent: SharedFlow<Unit> = _navigateToPrivatePolicyEvent.asSharedFlow()

    private val _showLogoutDialogEvent = MutableSharedFlow<Unit>()
    val showLogoutDialogEvent: SharedFlow<Unit> = _showLogoutDialogEvent.asSharedFlow()

    private val _showWithdrawalDialogEvent = MutableSharedFlow<Unit>()
    val showWithdrawalDialogEvent: SharedFlow<Unit> = _showWithdrawalDialogEvent.asSharedFlow()

    fun updateMemberInfo(memberUpdateRequest: MemberUpdateRequest) = viewModelScope.launch {
        updateMemberInfoUseCase(memberUpdateRequest)
            .onEach {
                if (it is Result.Loading) _updateMemberInfoEvent.emit(Result.Loading)
                else _updateMemberInfoEvent.emit(Result.UnLoading)
            }.collect {
                _updateMemberInfoEvent.emit(it)
            }
    }

    fun notificationSwitchClick(check: Boolean) {
        if (memberNotificationReceive.value != check) {
            viewModelScope.launch {
                updateNotificationStatusUseCase(NotificationUpdateRequest(check))
                    .onEach {
                        if (it is Result.Loading) _updateToReceiveNotificationEvent.emit(Result.Loading)
                        else _updateToReceiveNotificationEvent.emit(Result.UnLoading)
                    }.collect {
                        _updateToReceiveNotificationEvent.emit(it)
                    }
            }
        }
    }

    fun setMemberInfo(memberResponse: MemberResponse) {
        _member.value = memberResponse
        setMemberName(memberResponse.memberName)
        _memberNotificationReceive.value = memberResponse.isPushAlarmUse
    }

    fun setMemberName(memberName: String) {
        _memberName.value = memberName
    }

    fun setMemberNotificationReceive(check: Boolean) {
        _memberNotificationReceive.value = check
    }

    fun profileClick() = viewModelScope.launch {
        _showUpdateProfileDialogEvent.emit(Unit)
    }

    fun privacyPolicyClick() = viewModelScope.launch {
        _navigateToPrivatePolicyEvent.emit(Unit)
    }

    fun contactClick() = viewModelScope.launch {
        _navigateToContactEvent.emit(Unit)
    }

    fun logoutClick() = viewModelScope.launch {
        _showLogoutDialogEvent.emit(Unit)
    }

    fun withdrawalClick() = viewModelScope.launch {
        _showWithdrawalDialogEvent.emit(Unit)
    }

    fun logout() = viewModelScope.launch {
        logOutUseCase()
            .onEach {
                if (it is Result.Loading) _logoutEvent.emit(Result.Loading)
                else _logoutEvent.emit(Result.UnLoading)
            }.collect {
                _logoutEvent.emit(it)
            }
    }

    fun removeAuthToken() = viewModelScope.launch {
        removeAuthTokenUseCase()
    }

    companion object {
        private const val KEY_MEMBER_NAME = "memberName"
        private const val KEY_MEMBER_NOTIFICATION_RECEIVE = "notificationReceive"
    }
}