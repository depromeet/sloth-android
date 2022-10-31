package com.depromeet.sloth.ui.manage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.Member
import com.depromeet.sloth.data.network.member.MemberLogoutState
import com.depromeet.sloth.data.network.member.MemberState
import com.depromeet.sloth.data.network.member.MemberUpdateRequest
import com.depromeet.sloth.data.network.member.MemberUpdateResponse
import com.depromeet.sloth.data.network.member.MemberUpdateState
import com.depromeet.sloth.data.network.notification.NotificationState
import com.depromeet.sloth.data.network.notification.update.NotificationUpdateRequest
import com.depromeet.sloth.data.repository.MemberRepository
import com.depromeet.sloth.data.repository.NotificationRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
    private val notificationRepository: NotificationRepository
) : BaseViewModel(memberRepository) {

    private val _memberState = MutableLiveData<MemberState<Member>>()
    val memberState: LiveData<MemberState<Member>>
        get() = _memberState

    private val _memberUpdateState =
        MutableSharedFlow<MemberUpdateState<MemberUpdateResponse>>()
    val memberUpdateState: SharedFlow<MemberUpdateState<MemberUpdateResponse>>
        get() = _memberUpdateState

    private val _notificationReceiveState = MutableSharedFlow<NotificationState<String>>()
    val notificationReceiveState: SharedFlow<NotificationState<String>>
        get() = _notificationReceiveState

    private val _memberLogoutState = MutableLiveData<MemberLogoutState<String>>()
    val memberLogoutState: LiveData<MemberLogoutState<String>>
        get() = _memberLogoutState

    private val _member = MutableLiveData<Member>()
    val member: LiveData<Member>
        get() = _member

    private val _profileClick = MutableSharedFlow<Unit>()
    val profileClick: SharedFlow<Unit>
        get() = _profileClick

    private val _contactClick = MutableSharedFlow<Unit>()
    val contactButtonClick: SharedFlow<Unit>
        get() = _contactClick

    private val _privatePolicyClick = MutableSharedFlow<Unit>()
    val privatePolicyClick: SharedFlow<Unit>
        get() = _privatePolicyClick

    private val _logoutClick = MutableSharedFlow<Unit>()
    val logoutClick: SharedFlow<Unit>
        get() = _logoutClick

    private val _withdrawalClick = MutableSharedFlow<Unit>()
    val withdrawalClick: SharedFlow<Unit>
        get() = _withdrawalClick

    init {
        fetchMemberInfo()
    }

    fun fetchMemberInfo() = viewModelScope.launch {
        _memberState.value = MemberState.Loading
        val memberInfoResponse = memberRepository.fetchMemberInfo()
        _memberState.value = memberInfoResponse
    }

    fun updateMemberInfo(memberUpdateRequest: MemberUpdateRequest) = viewModelScope.launch {
        _memberUpdateState.emit(MemberUpdateState.Loading)
        val memberUpdateInfoResponse =
            memberRepository.updateMemberInfo(memberUpdateRequest)
        _memberUpdateState.emit(memberUpdateInfoResponse)
    }

    fun notiSwitchBtnClick(check: Boolean) = viewModelScope.launch {
        if (check != _member.value!!.isPushAlarmUse) {
            _notificationReceiveState.emit(NotificationState.Loading)
            val notificationUpdateResponse = notificationRepository.updateNotificationStatus(
                NotificationUpdateRequest(check)
            )
            _notificationReceiveState.emit(notificationUpdateResponse)
        }
    }

    fun setMemberInfo(member: Member) {
        _member.value = member
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
        _memberLogoutState.value = MemberLogoutState.Loading
        val memberLogoutState = memberRepository.logout()
        _memberLogoutState.value = memberLogoutState
    }
}