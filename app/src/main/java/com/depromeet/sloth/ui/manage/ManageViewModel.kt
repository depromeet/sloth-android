package com.depromeet.sloth.ui.manage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.Member
import com.depromeet.sloth.data.network.member.*
import com.depromeet.sloth.ui.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
) : ViewModel() {
    private val _memberState = MutableLiveData<MemberState<MemberInfoResponse>>()
    val memberState: LiveData<MemberState<MemberInfoResponse>> = _memberState

    private val _memberUpdateState = MutableLiveData<Event<MemberUpdateState<MemberUpdateInfoResponse>>>()
    val memberUpdateState: LiveData<Event<MemberUpdateState<MemberUpdateInfoResponse>>> = _memberUpdateState

//    private val _memberUpdateState = MutableLiveData<MemberUpdateState<MemberUpdateInfoResponse>>()
//    val memberUpdateState: LiveData<MemberUpdateState<MemberUpdateInfoResponse>> = _memberUpdateState

    private val _memberLogoutState = MutableLiveData<MemberLogoutState<String>>()
    val memberLogoutState: LiveData<MemberLogoutState<String>> = _memberLogoutState

    private val _member = MutableLiveData<Member>()
    val member: LiveData<Member> = _member

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
//        _memberUpdateState.value = MemberUpdateState.Loading
        val memberUpdateState =
            memberRepository.updateMemberInfo(memberUpdateInfoRequest)
        _memberUpdateState.value = Event(memberUpdateState)
//        _memberUpdateState.value = memberUpdateState
    }

    fun setMemberInfo(memberInfoResponse: MemberInfoResponse) = with(memberInfoResponse) {
        _member.value = Member(email, memberId, memberName)
    }

    fun logout() = viewModelScope.launch {
        _memberLogoutState.value = MemberLogoutState.Loading
        val memberLogoutState = memberRepository.logout()
        _memberLogoutState.value = memberLogoutState
    }

    fun removeAuthToken() = viewModelScope.launch {
        memberRepository.removeAuthToken()
    }
}