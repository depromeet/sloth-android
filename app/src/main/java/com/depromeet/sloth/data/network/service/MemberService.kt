package com.depromeet.sloth.data.network.service

import com.depromeet.sloth.data.model.response.member.MemberResponse
import com.depromeet.sloth.data.model.request.member.MemberUpdateRequest
import com.depromeet.sloth.data.model.response.member.MemberUpdateResponse
import retrofit2.Response
import retrofit2.http.*

interface MemberService {
    @GET("api/member")
    suspend fun fetchMemberInfo(
        @Header("Authorization") accessToken: String?
    ): Response<MemberResponse>?

    @PATCH("api/member")
    suspend fun updateMemberInfo(
        @Header("Authorization") accessToken: String?,
        @Body memberUpdateRequest: MemberUpdateRequest
    ): Response<MemberUpdateResponse>?

    @POST("api/logout")
    suspend fun logout(
        @Header("Authorization") accessToken: String?,
    ): Response<String>?
}