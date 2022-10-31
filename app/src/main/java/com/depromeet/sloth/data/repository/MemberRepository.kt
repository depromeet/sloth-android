package com.depromeet.sloth.data.repository

import com.depromeet.sloth.data.model.Member
import com.depromeet.sloth.data.network.member.MemberUpdateRequest
import com.depromeet.sloth.data.network.member.MemberUpdateResponse
import com.depromeet.sloth.ui.common.UiState

interface MemberRepository {

    suspend fun fetchMemberInfo(): UiState<Member>

    suspend fun updateMemberInfo(
        memberUpdateRequest: MemberUpdateRequest,
    ): UiState<MemberUpdateResponse>

    suspend fun logout(): UiState<String>

    fun removeAuthToken()
}