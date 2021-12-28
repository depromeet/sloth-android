package com.depromeet.sloth.data.network.lesson

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface LessonService {
    @GET("api/lesson/detail/{lessonId}")
    suspend fun fetchLessonDetail(@Path("lessonId") lessonId: String): Response<LessonDetailResponse>?

    @DELETE("api/lesson/{lessonId}")
    suspend fun deleteLesson(@Path("lessonId") lessonId: String): Response<LessonDeleteResponse>?
}