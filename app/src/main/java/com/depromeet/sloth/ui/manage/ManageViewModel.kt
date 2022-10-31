package com.depromeet.sloth.ui.manage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.Member
import com.depromeet.sloth.data.network.member.MemberUpdateRequest
import com.depromeet.sloth.data.network.member.MemberUpdateResponse
import com.depromeet.sloth.data.network.notification.update.NotificationUpdateRequest
import com.depromeet.sloth.data.repository.MemberRepository
import com.depromeet.sloth.data.repository.NotificationRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.common.UiState
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

    private val _memberState = MutableLiveData<UiState<Member>>()
    val memberState: LiveData<UiState<Member>>
        get() = _memberState

    private val _memberUpdateState =
        MutableSharedFlow<UiState<MemberUpdateResponse>>()
    val memberUpdateState: SharedFlow<UiState<MemberUpdateResponse>>
        get() = _memberUpdateState

    private val _notificationReceiveState = MutableSharedFlow<UiState<String>>()
    val notificationReceiveState: SharedFlow<UiState<String>>
        get() = _notificationReceiveState

    private val _memberLogoutState = MutableLiveData<UiState<String>>()
    val memberLogoutState: LiveData<UiState<String>>
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
        _memberState.value = UiState.Loading
        _memberState.value = memberRepository.fetchMemberInfo()
    }

    fun updateMemberInfo(memberUpdateRequest: MemberUpdateRequest) = viewModelScope.launch {
        _memberUpdateState.emit(UiState.Loading)
        _memberUpdateState.emit(memberRepository.updateMemberInfo(memberUpdateRequest))
    }

    fun notiSwitchBtnClick(check: Boolean) = viewModelScope.launch {
        if (check != _member.value!!.isPushAlarmUse) {
            _notificationReceiveState.emit(UiState.Loading)
            _notificationReceiveState.emit(
                notificationRepository.updateNotificationStatus(NotificationUpdateRequest(check))
            )
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
        _memberLogoutState.value = UiState.Loading
        _memberLogoutState.value = memberRepository.logout()
    }
}