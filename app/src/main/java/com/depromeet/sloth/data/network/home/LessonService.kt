package com.depromeet.sloth.data.network.home

import retrofit2.Response
import retrofit2.http.GET

interface LessonService {
    @GET("api/lesson/doing")
    suspend fun fetchTodayLessonList(): Response<List<TodayLessonResponse>>?

    @GET("api/lesson/list")
    suspend fun fetchAllLessonList(): Response<List<AllLessonResponse>>?
}