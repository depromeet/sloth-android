package com.depromeet.sloth.ui.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.member.*
import com.depromeet.sloth.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import java.lang.reflect.Member
import javax.inject.Inject

@HiltViewModel
class ManageViewModel @Inject constructor(
    private val memberRepository: MemberRepository
) : ViewModel() {
    suspend fun fetchMemberInfo(accessToken: String): MemberState<MemberInfoResponse> =
        withContext(viewModelScope.coroutineContext) {
            memberRepository.fetchMemberInfo(
                accessToken = accessToken
            )
        }

    suspend fun updateMemberInfo(
        accessToken: String,
        memberUpdateInfoRequest: MemberUpdateInfoRequest
    ): MemberState<MemberUpdateInfoResponse> =
        withContext(viewModelScope.coroutineContext) {
            memberRepository.updateMemberInfo(
                accessToken = accessToken,
                memberUpdateInfoRequest = memberUpdateInfoRequest
            )
        }

    suspend fun logout(
        accessToken: String
    ): MemberLogoutState<String> =
        withContext(viewModelScope.coroutineContext) {
            memberRepository.logout(
                accessToken = accessToken
            )
        }
}