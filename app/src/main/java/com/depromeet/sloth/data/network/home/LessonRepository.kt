package com.depromeet.sloth.data.network.home

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.network.ServiceGenerator

class LessonRepository {
    suspend fun fetchTodayLessonList(
        accessToken: String
    ): LessonState<List<TodayLessonResponse>> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(LessonService::class.java)
            .fetchTodayLessonList()?.run {
                return when (this.code()) {
                    200 -> LessonState.Success(this.body() ?: listOf(TodayLessonResponse.EMPTY))
                    401 -> LessonState.Unauthorized
                    403 -> LessonState.Forbidden
                    404 -> LessonState.NotFound
                    else -> LessonState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }

    suspend fun fetchAllLessonList(
        accessToken: String
    ): LessonState<List<AllLessonResponse>> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(LessonService::class.java)
            .fetchAllLessonList()?.run {
                return when (this.code()) {
                    200 -> LessonState.Success(this.body() ?: listOf())
                    401 -> LessonState.Unauthorized
                    403 -> LessonState.Forbidden
                    404 -> LessonState.NotFound
                    else -> LessonState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }
}