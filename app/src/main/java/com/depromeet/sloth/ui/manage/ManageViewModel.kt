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
    val updateNotificationReceiveEvent: SharedFlow<Result<String>> =
        _updateToReceiveNotificationEvent.asSharedFlow()

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

    private val _navigateToUpdateProfileDialogEvent = MutableSharedFlow<Unit>()
    val navigateToUpdateProfileDialogEvent: SharedFlow<Unit> = _navigateToUpdateProfileDialogEvent.asSharedFlow()

    private val _navigateToContactEvent = MutableSharedFlow<Unit>()
    val navigateToContactEvent: SharedFlow<Unit> = _navigateToContactEvent.asSharedFlow()

    private val _navigateToPrivatePolicyEvent = MutableSharedFlow<String>()
    val navigateToPrivatePolicyEvent: SharedFlow<String> = _navigateToPrivatePolicyEvent.asSharedFlow()

    private val _navigateToLogoutDialogEvent = MutableSharedFlow<Unit>()
    val navigateToLogoutDialogEvent: SharedFlow<Unit> = _navigateToLogoutDialogEvent.asSharedFlow()

    private val _navigateToWithdrawalDialogEvent = MutableSharedFlow<Unit>()
    val navigateToWithdrawalDialogEvent: SharedFlow<Unit> = _navigateToWithdrawalDialogEvent.asSharedFlow()

    fun updateMemberInfo(memberUpdateRequest: MemberUpdateRequest) = viewModelScope.launch {
        updateMemberInfoUseCase(memberUpdateRequest)
            .onEach {
                if (it is Result.Loading) _updateMemberInfoEvent.emit(Result.Loading)
                else _updateMemberInfoEvent.emit(Result.UnLoading)
            }.collect {
                _updateMemberInfoEvent.emit(it)
            }
    }

    fun updateNotificationSwitch(check: Boolean) {
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

    fun navigateToUpdateProfileDialog() = viewModelScope.launch {
        _navigateToUpdateProfileDialogEvent.emit(Unit)
    }

    fun navigateToPrivacyPolicy() = viewModelScope.launch {
        _navigateToPrivatePolicyEvent.emit(TAG)
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
        private const val TAG = "ManageFragment"
        private const val KEY_MEMBER_NAME = "memberName"
        private const val KEY_MEMBER_NOTIFICATION_RECEIVE = "notificationReceive"
    }
}