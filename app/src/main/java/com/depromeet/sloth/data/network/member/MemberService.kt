package com.depromeet.sloth.data.network.member

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface MemberService {
    @GET("api/member")
    suspend fun fetchMemberInfo(): Response<MemberInfoResponse>?

    @PATCH("api/member")
    suspend fun updateMemberInfo(@Body requestUpdate: MemberUpdateInfoRequest): Response<MemberUpdateInfoResponse>?
}