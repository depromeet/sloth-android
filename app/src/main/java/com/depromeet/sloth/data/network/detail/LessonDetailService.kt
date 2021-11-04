package com.depromeet.sloth.data.network.detail

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface LessonDetailService {
    @GET("api/lesson/detail/{lessonId}")
    suspend fun fetchLessonDetailInfo(@Path("lessonId") lessonId: String): Response<LessonDetailResponse>?
}