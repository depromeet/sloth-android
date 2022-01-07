package com.depromeet.sloth.ui.manage

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.member.*
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.withContext

class ManageViewModel @ViewModelInject constructor(
    private val memberRepository: MemberRepository
) : BaseViewModel() {
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
}