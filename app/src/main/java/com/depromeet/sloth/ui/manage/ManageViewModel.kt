package com.depromeet.sloth.ui.manage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.common.Result
import com.depromeet.sloth.data.model.request.member.MemberUpdateRequest
import com.depromeet.sloth.data.model.request.notification.NotificationUpdateRequest
import com.depromeet.sloth.data.model.response.member.MemberResponse
import com.depromeet.sloth.data.model.response.member.MemberUpdateResponse
import com.depromeet.sloth.domain.repository.MemberRepository
import com.depromeet.sloth.domain.repository.NotificationRepository
import com.depromeet.sloth.extensions.getMutableStateFlow
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ManageViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
    private val notificationRepository: NotificationRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(memberRepository) {

    val memberState: Flow<Result<MemberResponse>> = memberRepository.fetchMemberInfo()

//    val memberState: Flow<Result<MemberResponse>> =
//        memberRepository.fetchMemberInfo().asResult()

    private val _memberUpdateState = MutableSharedFlow<Result<MemberUpdateResponse>>()
    val memberUpdateState: SharedFlow<Result<MemberUpdateResponse>> =
        _memberUpdateState.asSharedFlow()

    private val _notificationReceiveState = MutableSharedFlow<Result<String>>()
    val notificationReceiveState: SharedFlow<Result<String>> =
        _notificationReceiveState.asSharedFlow()

    // api response 변경 후
//    private val _notificationReceiveState = MutableSharedFlow<Result<NotificationUpdateResponse>>()
//    val notificationReceiveState: SharedFlow<Result<NotificationUpdateResponse>> =
//        _notificationReceiveState.asSharedFlow()

    private val _memberLogoutState = MutableSharedFlow<Result<String>>()
    val memberLogoutState: SharedFlow<Result<String>> = _memberLogoutState.asSharedFlow()

    private val _member = MutableStateFlow(MemberResponse())
    val member: StateFlow<MemberResponse> = _member.asStateFlow()

    private val _memberName =
        savedStateHandle.getMutableStateFlow(KEY_MEMBER_NAME, DEFAULT_STRING_VALUE)
    val memberName: StateFlow<String> = _memberName.asStateFlow()

    private val _memberNotificationReceive =
        savedStateHandle.getMutableStateFlow(KEY_MEMBER_NOTIFICATION_RECEIVE, false)
    val memberNotificationReceive: StateFlow<Boolean> = _memberNotificationReceive.asStateFlow()

    private val _profileClick = MutableSharedFlow<Unit>()
    val profileClick: SharedFlow<Unit> = _profileClick.asSharedFlow()

    private val _contactClick = MutableSharedFlow<Unit>()
    val contactButtonClick: SharedFlow<Unit> = _contactClick.asSharedFlow()

    private val _privatePolicyClick = MutableSharedFlow<Unit>()
    val privatePolicyClick: SharedFlow<Unit> = _privatePolicyClick.asSharedFlow()

    private val _logoutClick = MutableSharedFlow<Unit>()
    val logoutClick: SharedFlow<Unit> = _logoutClick.asSharedFlow()

    private val _withdrawalClick = MutableSharedFlow<Unit>()
    val withdrawalClick: SharedFlow<Unit> = _withdrawalClick.asSharedFlow()

    fun updateMemberInfo(memberUpdateRequest: MemberUpdateRequest) = viewModelScope.launch {
        memberRepository.updateMemberInfo(memberUpdateRequest)
            .onEach {
                if (it is Result.Loading) _memberUpdateState.emit(Result.Loading)
                else _memberUpdateState.emit(Result.UnLoading)
            }.collect {
                _memberUpdateState.emit(it)
            }
    }

    fun notificationSwitchClick(check: Boolean) {
        if (memberNotificationReceive.value != check) {
            viewModelScope.launch {
                notificationRepository.updateNotificationStatus(NotificationUpdateRequest(check))
                    .onEach {
                        if (it is Result.Loading) _notificationReceiveState.emit(Result.Loading)
                        else _notificationReceiveState.emit(Result.UnLoading)
                    }.collect {
                        _notificationReceiveState.emit(it)
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
        _profileClick.emit(Unit)
    }

    fun privacyPolicyClick() = viewModelScope.launch {
        _privatePolicyClick.emit(Unit)
    }

    fun contactClick() = viewModelScope.launch {
        _contactClick.emit(Unit)
    }

    fun logoutClick() = viewModelScope.launch {
        _logoutClick.emit(Unit)
    }

    fun withdrawalClick() = viewModelScope.launch {
        _withdrawalClick.emit(Unit)
    }

    fun logout() = viewModelScope.launch {
        memberRepository.logout()
            .onEach {
                if (it is Result.Loading) _memberLogoutState.emit(Result.Loading)
                else _memberLogoutState.emit(Result.UnLoading)
            }.collect {
                _memberLogoutState.emit(it)
            }
    }

    companion object {
        private const val KEY_MEMBER_NAME = "memberName"
        private const val KEY_MEMBER_NOTIFICATION_RECEIVE = "notificationReceive"
    }
}