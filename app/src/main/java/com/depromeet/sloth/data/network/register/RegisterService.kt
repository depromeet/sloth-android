package com.depromeet.sloth.data.network.register

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface RegisterService {
    @POST("api/lesson")
    suspend fun registerLesson(@Body request: RegisterLessonRequest): Response<RegisterLessonResponse>?

    @PATCH("api/member/")
    suspend fun registerNickname(@Body request: RegisterNicknameRequest): Int?
}