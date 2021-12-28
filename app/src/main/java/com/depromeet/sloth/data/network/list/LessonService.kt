package com.depromeet.sloth.data.network.list

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface LessonService {
    @GET("api/lesson/doing")
    suspend fun fetchTodayLessonList(): Response<List<LessonTodayResponse>>?

    @GET("api/lesson/list")
    suspend fun fetchAllLessonList(): Response<List<LessonInfoResponse>>?

    @PATCH("api/lesson/number")
    suspend fun updateLessonCount(@Body request: LessonUpdateCountRequest): Response<LessonUpdateCountResponse>?
}