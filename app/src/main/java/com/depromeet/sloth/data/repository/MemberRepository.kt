package com.depromeet.sloth.data.repository

import com.depromeet.sloth.data.model.response.member.MemberResponse
import com.depromeet.sloth.data.model.request.member.MemberUpdateRequest
import com.depromeet.sloth.data.model.response.member.MemberUpdateResponse
import com.depromeet.sloth.common.Result
import kotlinx.coroutines.flow.Flow

interface MemberRepository {

    // suspend fun fetchMemberInfo(): Result<MemberResponse>
    fun fetchMemberInfo(): Flow<Result<MemberResponse>>

//    suspend fun updateMemberInfo(
//        memberUpdateRequest: MemberUpdateRequest,
//    ): Result<MemberUpdateResponse>

    fun updateMemberInfo(
        memberUpdateRequest: MemberUpdateRequest,
    ): Flow<Result<MemberUpdateResponse>>

    suspend fun logout(): Result<String>

    fun removeAuthToken()
}