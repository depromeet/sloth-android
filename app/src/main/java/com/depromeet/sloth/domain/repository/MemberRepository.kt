package com.depromeet.sloth.domain.repository

import com.depromeet.sloth.util.Result
import com.depromeet.sloth.data.model.request.member.MemberUpdateRequest
import com.depromeet.sloth.data.model.response.member.MemberResponse
import com.depromeet.sloth.data.model.response.member.MemberUpdateResponse
import kotlinx.coroutines.flow.Flow

interface MemberRepository {

     fun fetchMemberInfo(): Flow<Result<MemberResponse>>

    // fun fetchMemberInfo(): Flow<MemberResponse>

    fun updateMemberInfo(
        memberUpdateRequest: MemberUpdateRequest,
    ): Flow<Result<MemberUpdateResponse>>

    fun logout(): Flow<Result<String>>

    suspend fun deleteAuthToken()
}