package com.depromeet.sloth.data.network.home

import retrofit2.Response
import retrofit2.http.GET

interface LessonService {
    @GET("api/lesson/doing")
    suspend fun fetchLessonWeeklyList(): Response<List<WeeklyLessonResponse>>?

    @GET("api/lesson/list")
    suspend fun fetchLessonAllList(): Response<List<AllLessonResponse>>?
}