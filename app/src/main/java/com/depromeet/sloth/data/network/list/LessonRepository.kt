package com.depromeet.sloth.data.network.list

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.network.ServiceGenerator

class LessonRepository {
    suspend fun fetchTodayLessonList(
        accessToken: String
    ): LessonState<List<LessonTodayResponse>> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(LessonService::class.java)
            .fetchTodayLessonList()?.run {
                return when (this.code()) {
                    200 -> LessonState.Success(this.body() ?: listOf(LessonTodayResponse.EMPTY))
                    401 -> LessonState.Unauthorized
                    403 -> LessonState.Forbidden
                    404 -> LessonState.NotFound
                    else -> LessonState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }

    suspend fun fetchAllLessonList(
        accessToken: String
    ): LessonState<List<LessonInfoResponse>> {
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

    suspend fun updateLessonCount(
        accessToken: String,
        count: Int,
        lessonId: Int
    ): LessonState<LessonUpdateCountResponse> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(LessonService::class.java)
            .updateLessonCount(
                LessonUpdateCountRequest(
                    count = count,
                    lessonId = lessonId
                )
            )?.run {
                return when (this.code()) {
                    200 -> LessonState.Success(this.body() ?: LessonUpdateCountResponse.EMPTY)
                    401 -> LessonState.Unauthorized
                    403 -> LessonState.Forbidden
                    404 -> LessonState.NotFound
                    else -> LessonState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }
}