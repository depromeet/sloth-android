package com.depromeet.sloth.data.network.member

import retrofit2.Response
import retrofit2.http.GET

interface MemberInfoService {
    @GET("api/member")
    suspend fun fetchMemberInfo(): Response<MemberInfoResponse>?
}