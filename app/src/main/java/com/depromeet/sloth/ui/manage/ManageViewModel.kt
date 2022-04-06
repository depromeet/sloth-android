package com.depromeet.sloth.ui.manage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.lesson.LessonState
import com.depromeet.sloth.data.network.member.*
import com.depromeet.sloth.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Member
import javax.inject.Inject

@HiltViewModel
class ManageViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
) : ViewModel() {
    private val _memberState = MutableLiveData<MemberState<MemberInfoResponse>>()
    val memberState: LiveData<MemberState<MemberInfoResponse>> = _memberState

    private val _memberUpdateState = MutableLiveData<MemberUpdateState<MemberUpdateInfoResponse>>()
    val memberUpdateState: LiveData<MemberUpdateState<MemberUpdateInfoResponse>> =
        _memberUpdateState

    private val _memberLogoutState = MutableLiveData<MemberLogoutState<String>>()
    val memberLogoutState: LiveData<MemberLogoutState<String>> = _memberLogoutState

    private val _member = MutableLiveData<MemberInfoResponse>()
    val member: LiveData<MemberInfoResponse> = _member

    init {
        fetchMemberInfo()
    }

    private fun fetchMemberInfo() = viewModelScope.launch {
        _memberState.value = MemberState.Loading
        val memberInfoResponse = memberRepository.fetchMemberInfo()
        Log.d("ManageViewModel", "fetchMemberInfo: $memberInfoResponse")
        _memberState.value = memberInfoResponse
    }

    fun updateMemberInfo(memberUpdateInfoRequest: MemberUpdateInfoRequest) = viewModelScope.launch {
        _memberUpdateState.value = MemberUpdateState.Loading
        val memberUpdateState =
            memberRepository.updateMemberInfo(memberUpdateInfoRequest = memberUpdateInfoRequest)
        _memberUpdateState.value = memberUpdateState
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