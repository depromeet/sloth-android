package com.depromeet.sloth.ui.manage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.Member
import com.depromeet.sloth.data.network.member.*
import com.depromeet.sloth.data.network.notification.NotificationState
import com.depromeet.sloth.data.network.notification.update.NotificationUpdateRequest
import com.depromeet.sloth.data.repository.MemberRepository
import com.depromeet.sloth.data.repository.NotificationRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ManageViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
    private val notificationRepository: NotificationRepository
) : BaseViewModel(memberRepository) {

    private val _memberState = MutableLiveData<MemberState<Member>>()
    val memberState: LiveData<MemberState<Member>>
        get() = _memberState

//    private val _member = MutableStateFlow<Member>(Member())
//    val member: StateFlow<Member> = _member

    private val _memberUpdateState =
        MutableLiveData<Event<MemberUpdateState<MemberUpdateInfoResponse>>>()
    val memberUpdateState: LiveData<Event<MemberUpdateState<MemberUpdateInfoResponse>>>
        get() = _memberUpdateState

//    private val _memberUpdateState = MutableStateFlow<MemberUpdateInfoResponse>(MemberUpdateInfoResponse())
//    val memberUpdateState: StateFlow<MemberUpdateInfoResponse> = _memberUpdateState.asStateFlow()

    private val _notificationReceiveState = MutableLiveData<Event<NotificationState<String>>>()
    val notificationReceiveState: LiveData<Event<NotificationState<String>>>
        get() = _notificationReceiveState

    private val _memberLogoutState = MutableLiveData<MemberLogoutState<String>>()
    val memberLogoutState: LiveData<MemberLogoutState<String>>
        get() = _memberLogoutState

    private val _member = MutableLiveData<Member>()
    val member: LiveData<Member>
        get() = _member

    private val _profileClick = MutableLiveData<Event<Unit>>()
    val profileClick: LiveData<Event<Unit>>
        get() = _profileClick

    private val _contactClick = MutableLiveData<Event<Unit>>()
    val contactButtonClick: LiveData<Event<Unit>>
        get() = _contactClick

    private val _privatePolicyClick = MutableLiveData<Event<Unit>>()
    val privatePolicyClick: LiveData<Event<Unit>>
        get() = _privatePolicyClick

    private val _logoutClick = MutableLiveData<Event<Unit>>()
    val logoutClick: LiveData<Event<Unit>>
        get() = _logoutClick

    private val _withdrawalClick = MutableLiveData<Event<Unit>>()
    val withdrawalClick: LiveData<Event<Unit>>
        get() = _withdrawalClick

    init {
        fetchMemberInfo()
    }

    fun fetchMemberInfo() = viewModelScope.launch {
        _memberState.value = MemberState.Loading
        val memberInfoResponse = memberRepository.fetchMemberInfo()
        _memberState.value = memberInfoResponse
    }

    fun updateMemberInfo(memberUpdateInfoRequest: MemberUpdateInfoRequest) = viewModelScope.launch {
        _memberUpdateState.value = Event(MemberUpdateState.Loading)
        val memberUpdateState =
            memberRepository.updateMemberInfo(memberUpdateInfoRequest)
        _memberUpdateState.value = Event(memberUpdateState)
    }

    fun notiSwitchBtnClick(check: Boolean) = viewModelScope.launch {
        if (check != _member.value!!.isPushAlarmUse) {
            Timber.tag("notiSwitchBtnClick").d("호출")
            _notificationReceiveState.value = Event(NotificationState.Loading)
            val notificationReceiveState = notificationRepository.updateNotificationStatus(
                NotificationUpdateRequest(check)
            )
            _notificationReceiveState.value = Event(notificationReceiveState)
        }
    }

    fun setMemberInfo(member: Member) {
        _member.value = member
    }

    fun profileClick() {
        _profileClick.value = Event(Unit)
    }

    fun privacyPolicyClick() {
        _privatePolicyClick.value = Event(Unit)
    }

    fun contactClick() {
        _contactClick.value = Event(Unit)
    }

    fun logoutClick() {
        _logoutClick.value = Event(Unit)
    }

    fun withdrawalClick() {
        _withdrawalClick.value = Event(Unit)
    }

    fun logout() = viewModelScope.launch {
        _memberLogoutState.value = MemberLogoutState.Loading
        val memberLogoutState = memberRepository.logout()
        _memberLogoutState.value = memberLogoutState
    }
}