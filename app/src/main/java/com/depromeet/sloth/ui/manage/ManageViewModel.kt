package com.depromeet.sloth.ui.manage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.Member
import com.depromeet.sloth.data.network.member.*
import com.depromeet.sloth.data.repository.MemberRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
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

    private val _memberLogoutState = MutableLiveData<MemberLogoutState<String>>()
    val memberLogoutState: LiveData<MemberLogoutState<String>>
        get() = _memberLogoutState

    private val _member = MutableLiveData<Member>()
    val member: LiveData<Member>
        get() = _member

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

//    fun updateMemberInfo(memberUpdateInfoRequest: MemberUpdateInfoRequest) = viewModelScope.launch {
//        val memberUpdateState = memberRepository.updateMemberInfo(memberUpdateInfoRequest)
//        //_memberUpdateState.value = memberUpdateState
//    }

    fun setMemberInfo(member: Member) {
        _member.value = member
    }

    fun logout() = viewModelScope.launch {
        _memberLogoutState.value = MemberLogoutState.Loading
        val memberLogoutState = memberRepository.logout()
        _memberLogoutState.value = memberLogoutState
    }
}