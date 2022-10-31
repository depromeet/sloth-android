package com.depromeet.sloth.data.network.member

import com.depromeet.sloth.data.model.Member
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface MemberService {
    @GET("api/member")
    suspend fun fetchMemberInfo(): Response<Member>?

    @PATCH("api/member")
    suspend fun updateMemberInfo(@Body memberUpdateRequest: MemberUpdateRequest): Response<MemberUpdateResponse>?

    @POST("api/logout")
    suspend fun logout(): Response<String>?
}