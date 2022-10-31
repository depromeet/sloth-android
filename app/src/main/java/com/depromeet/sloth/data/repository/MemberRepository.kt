package com.depromeet.sloth.data.repository

import com.depromeet.sloth.data.model.Member
import com.depromeet.sloth.data.network.member.MemberLogoutState
import com.depromeet.sloth.data.network.member.MemberState
import com.depromeet.sloth.data.network.member.MemberUpdateRequest
import com.depromeet.sloth.data.network.member.MemberUpdateResponse
import com.depromeet.sloth.data.network.member.MemberUpdateState

interface MemberRepository {

    suspend fun fetchMemberInfo(): MemberState<Member>

    suspend fun updateMemberInfo(
        memberUpdateRequest: MemberUpdateRequest,
    ): MemberUpdateState<MemberUpdateResponse>

    suspend fun logout(): MemberLogoutState<String>

    fun removeAuthToken()
}