package com.depromeet.sloth.data.network.home

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.network.ServiceGenerator

class LessonRepository {
    suspend fun fetchLessonWeeklyList(
        accessToken: String
    ): LessonState<List<WeeklyLessonResponse>> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(LessonService::class.java)
            .fetchLessonWeeklyList()?.run {
                return when (this.code()) {
                    200 -> LessonState.Success(this.body() ?: listOf())
                    401 -> LessonState.Unauthorized
                    403 -> LessonState.Forbidden
                    404 -> LessonState.NotFound
                    else -> LessonState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }

    suspend fun fetchLessonAllList(
        accessToken: String
    ): LessonState<List<AllLessonResponse>> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(LessonService::class.java)
            .fetchLessonAllList()?.run {
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