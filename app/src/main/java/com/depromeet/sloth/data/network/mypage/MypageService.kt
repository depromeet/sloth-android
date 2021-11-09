package com.depromeet.sloth.data.network.mypage

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface MypageService {

    @GET("api/member")
    suspend fun fetchMemberInfo(): Response<MypageResponse>?

    @PATCH("api/member")
    suspend fun updateMemberInfo(@Body request: MypageRequest): Int?
}