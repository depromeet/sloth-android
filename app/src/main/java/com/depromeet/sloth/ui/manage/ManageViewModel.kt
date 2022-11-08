package com.depromeet.sloth.ui.manage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.Member
import com.depromeet.sloth.data.network.member.MemberUpdateRequest
import com.depromeet.sloth.data.network.member.MemberUpdateResponse
import com.depromeet.sloth.data.network.notification.update.NotificationUpdateRequest
import com.depromeet.sloth.data.repository.MemberRepository
import com.depromeet.sloth.data.repository.NotificationRepository
import com.depromeet.sloth.extensions.getMutableStateFlow
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.common.Result
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

    private val _memberState = MutableSharedFlow<Result<Member>>()
    val memberState: SharedFlow<Result<Member>> = _memberState.asSharedFlow()

    private val _memberUpdateState =
        MutableSharedFlow<Result<MemberUpdateResponse>>()
    val memberUpdateState: SharedFlow<Result<MemberUpdateResponse>>
        get() = _memberUpdateState

    private val _notificationReceiveState = MutableSharedFlow<Result<String>>()
    val notificationReceiveState: SharedFlow<Result<String>>
        get() = _notificationReceiveState

    private val _memberLogoutState = MutableSharedFlow<Result<String>>()
    val memberLogoutState: SharedFlow<Result<String>> = _memberLogoutState.asSharedFlow()

    private val _member = MutableStateFlow(Member())
    val member: StateFlow<Member> = _member.asStateFlow()

    private val _memberName = savedStateHandle.getMutableStateFlow(KEY_MEMBER_NAME, DEFAULT_STRING_VALUE)
    val memberName: StateFlow<String> = _memberName.asStateFlow()

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

    init {
        fetchMemberInfo()
    }

    fun fetchMemberInfo() = viewModelScope.launch {
        _memberState.emit(Result.Loading)
        _memberState.emit(memberRepository.fetchMemberInfo())
    }

    fun updateMemberInfo(memberUpdateRequest: MemberUpdateRequest) = viewModelScope.launch {
        _memberUpdateState.emit(Result.Loading)
        _memberUpdateState.emit(memberRepository.updateMemberInfo(memberUpdateRequest))
    }

    fun notificationSwitchClick(check: Boolean) = viewModelScope.launch {
        if (check != member.value.isPushAlarmUse) {
            _notificationReceiveState.emit(Result.Loading)
            _notificationReceiveState.emit(
                notificationRepository.updateNotificationStatus(NotificationUpdateRequest(check))
            )
        }
    }

    fun setMemberInfo(member: Member) {
        _member.value = member
        _memberName.value = member.memberName
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
        _memberLogoutState.emit(Result.Loading)
        _memberLogoutState.emit(memberRepository.logout())
    }

    companion object {
        private const val KEY_MEMBER_NAME = "memberName"
    }
}