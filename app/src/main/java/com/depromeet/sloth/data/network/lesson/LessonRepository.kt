package com.depromeet.sloth.data.network.lesson

import android.os.Build
import androidx.annotation.RequiresApi
import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.network.ServiceGenerator

/**
 * @author 최철훈
 * @created 2021-12-28
 * @desc
 */
class LessonRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchLessonDetail(
        accessToken: String,
        lessonId: String
    ): LessonState<LessonDetailResponse> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(LessonService::class.java)
            .fetchLessonDetail(
                lessonId,
            )?.run {
                return when(this.code()) {
                    200 -> LessonState.Success(this.body() ?: LessonDetailResponse())
                    401 -> LessonState.Unauthorized
                    403 -> LessonState.Forbidden
                    404 -> LessonState.NotFound
                    else -> LessonState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }

    suspend fun deleteLesson(
        accessToken: String,
        lessonId: String
    ): LessonState<LessonDeleteResponse> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(LessonService::class.java)
            .deleteLesson(
                lessonId,
            )?.run {
                return when(this.code()) {
                    200 -> LessonState.Success(this.body() ?: LessonDeleteResponse())
                    401 -> LessonState.Unauthorized
                    403 -> LessonState.Forbidden
                    404 -> LessonState.NotFound
                    else -> LessonState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }
}