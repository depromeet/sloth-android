package com.depromeet.sloth.ui.manage

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.member.*
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.withContext

class ManageViewModel(
    preferenceManager: PreferenceManager
) : BaseViewModel() {
    private val memberRepository = MemberRepository(preferenceManager)

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