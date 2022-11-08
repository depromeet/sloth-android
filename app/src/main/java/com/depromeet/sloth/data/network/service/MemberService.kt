package com.depromeet.sloth.data.network.service

import com.depromeet.sloth.data.model.response.member.MemberResponse
import com.depromeet.sloth.data.model.request.member.MemberUpdateRequest
import com.depromeet.sloth.data.model.response.member.MemberUpdateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface MemberService {
    @GET("api/member")
    suspend fun fetchMemberInfo(): Response<MemberResponse>?

    @PATCH("api/member")
    suspend fun updateMemberInfo(@Body memberUpdateRequest: MemberUpdateRequest): Response<MemberUpdateResponse>?

    @POST("api/logout")
    suspend fun logout(): Response<String>?
}