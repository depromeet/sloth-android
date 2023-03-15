package com.depromeet.data.network.service

import com.depromeet.data.model.response.member.MemberResponse
import com.depromeet.data.model.request.member.MemberUpdateRequest
import com.depromeet.data.model.response.member.MemberUpdateResponse
import retrofit2.Response
import retrofit2.http.*


interface MemberService {
    @GET("api/member")
    suspend fun fetchMemberInfo(): Response<MemberResponse>?

    @PATCH("api/member")
    suspend fun updateMemberInfo(
        @Body memberUpdateRequest: MemberUpdateRequest
    ): Response<MemberUpdateResponse>?

    @POST("api/logout")
    suspend fun logout(): Response<String>?
}