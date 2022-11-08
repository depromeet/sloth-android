package com.depromeet.sloth.data.repository

import com.depromeet.sloth.data.model.Member
import com.depromeet.sloth.data.network.member.MemberUpdateRequest
import com.depromeet.sloth.data.network.member.MemberUpdateResponse
import com.depromeet.sloth.ui.common.Result

interface MemberRepository {

    suspend fun fetchMemberInfo(): Result<Member>

    suspend fun updateMemberInfo(
        memberUpdateRequest: MemberUpdateRequest,
    ): Result<MemberUpdateResponse>

    suspend fun logout(): Result<String>

    fun removeAuthToken()
}