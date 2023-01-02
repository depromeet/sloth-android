package com.depromeet.sloth.ui.manage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
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
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.item.Member
import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
import com.depromeet.sloth.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ManageViewModel @Inject constructor(
    private val getMemberInfoUseCase: GetMemberInfoUseCase,
    private val updateMemberInfoUseCase: UpdateMemberInfoUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val removeAuthTokenUseCase: RemoveAuthTokenUseCase,
    private val updateNotificationStatusUseCase: UpdateNotificationStatusUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

//    val fetchMemberInfoEvent: Flow<Result<MemberResponse>> = getMemberInfoUseCase()

//    val fetchMemberInfoEvent: Flow<Result<MemberResponse>> =
//        getMemberInfoUseCase().asResult()
    private val _memberName =
        savedStateHandle.getMutableStateFlow(KEY_MEMBER_NAME, DEFAULT_STRING_VALUE)
    val memberName: StateFlow<String> = _memberName.asStateFlow()

    private val _previousMemberName =
        savedStateHandle.getMutableStateFlow(KEY_PREVIOUS_MEMBER_NAME, DEFAULT_STRING_VALUE)
    val previousMemberName: StateFlow<String> = _previousMemberName.asStateFlow()

    private val _fetchMemberInfoEvent = MutableSharedFlow<Result<MemberResponse>>()
    val fetchMemberInfoEvent: SharedFlow<Result<MemberResponse>> =
        _fetchMemberInfoEvent.asSharedFlow()

    private val _updateMemberInfoEvent = MutableSharedFlow<Result<MemberUpdateResponse>>()
    val updateMemberInfoEvent: SharedFlow<Result<MemberUpdateResponse>> =
        _updateMemberInfoEvent.asSharedFlow()

    private val _updateToReceiveNotificationEvent = MutableSharedFlow<Result<String>>()
    val updateNotificationReceiveEvent: SharedFlow<Result<String>> =
        _updateToReceiveNotificationEvent.asSharedFlow()

    private val _logoutEvent = MutableSharedFlow<Result<String>>()
    val logoutEvent: SharedFlow<Result<String>> = _logoutEvent.asSharedFlow()

    private val _member = MutableStateFlow(Member())
    val member: StateFlow<Member> = _member.asStateFlow()

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
            .onEach {
                if (it is Result.Loading) _fetchMemberInfoEvent.emit(Result.Loading)
                else _fetchMemberInfoEvent.emit(Result.UnLoading)
            }.collect {
                _fetchMemberInfoEvent.emit(it)
            }
    }

    fun updateMemberInfo() = viewModelScope.launch {
        updateMemberInfoUseCase(MemberUpdateRequest(memberName.value))
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

    fun setPreviousMemberName(previousMemberName: String) {
        _previousMemberName.value = previousMemberName
    }

    fun setMemberNotificationReceive(check: Boolean) {
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

    override fun retry() {
        fetchMemberInfo()
    }

    companion object {
        private const val KEY_MEMBER_NAME = "memberName"
        private const val KEY_PREVIOUS_MEMBER_NAME = "previousMemberName"
        private const val KEY_MEMBER_NOTIFICATION_RECEIVE = "notificationReceive"
    }
}