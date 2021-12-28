package com.depromeet.sloth.data.network.lesson

import retrofit2.Response
import retrofit2.http.*

interface LessonService {
    @GET("api/lesson/doing")
    suspend fun fetchTodayLessonList(): Response<List<LessonTodayResponse>>?

    @GET("api/lesson/list")
    suspend fun fetchAllLessonList(): Response<List<LessonAllResponse>>?

    @PATCH("api/lesson/number")
    suspend fun updateLessonCount(@Body request: LessonUpdateCountRequest): Response<LessonUpdateCountResponse>?

    @GET("api/lesson/detail/{lessonId}")
    suspend fun fetchLessonDetail(@Path("lessonId") lessonId: String): Response<LessonDetailResponse>?

    @DELETE("api/lesson/{lessonId}")
    suspend fun deleteLesson(@Path("lessonId") lessonId: String): Response<LessonDeleteResponse>?
}