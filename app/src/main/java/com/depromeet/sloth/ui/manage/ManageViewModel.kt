package com.depromeet.sloth.ui.manage

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.member.*
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.*

class ManageViewModel : BaseViewModel() {
    private val memberRepository = MemberRepository()

    suspend fun fetchMemberInfo(
        accessToken: String,
    ): MemberState<MemberInfoResponse> = viewModelScope.async {
        memberRepository.fetchMemberInfo(
            accessToken = accessToken
        )
    }.await()

    suspend fun updateMemberInfo(
        accessToken: String,
        memberUpdateInfoRequest: MemberUpdateInfoRequest,
    ): MemberState<MemberUpdateInfoResponse> = viewModelScope.async {
        memberRepository.updateMemberInfo(
            accessToken = accessToken,
            memberUpdateInfoRequest = memberUpdateInfoRequest
        )
    }.await()

    suspend fun removeAuthToken(pm: PreferenceManager) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                pm.removeAuthToken()
            }
        }
}