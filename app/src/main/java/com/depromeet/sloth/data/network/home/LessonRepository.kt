package com.depromeet.sloth.data.network.home

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.network.ServiceGenerator

class LessonRepository {
    suspend fun fetchLessonList(
        accessToken: String
    ): LessonListState<List<LessonResponse>> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(LessonService::class.java)
            .fetchLessonList()?.run {
                return when (this.code()) {
                    200 -> LessonListState.Success(this.body() ?: listOf())
                    401 -> LessonListState.Unauthorized
                    403 -> LessonListState.Forbidden
                    404 -> LessonListState.NotFound
                    else -> LessonListState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonListState.Error(Exception("Retrofit Exception"))
    }
}