package com.depromeet.sloth.data.network.detail

import android.os.Build
import androidx.annotation.RequiresApi
import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.network.ServiceGenerator

class LessonDetailRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchLessonDetailInfo(
        accessToken: String,
        lessonId: String
    ): LessonDetailState<LessonDetailResponse> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(LessonDetailService::class.java)
            .fetchLessonDetailInfo(
                lessonId,
            )?.run {
                return when(this.code()) {
                    200 -> LessonDetailState.Success(this.body() ?: LessonDetailResponse())
                    401 -> LessonDetailState.Unauthorized
                    403 -> LessonDetailState.Forbidden
                    404 -> LessonDetailState.NotFound
                    else -> LessonDetailState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonDetailState.Error(Exception("Retrofit Exception"))
    }

    suspend fun deleteLesson(
        accessToken: String,
        lessonId: String
    ): LessonDetailState<DeleteLessonResponse> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(LessonDetailService::class.java)
            .deleteLesson(
                lessonId,
            )?.run {
                return when(this.code()) {
                    200 -> LessonDetailState.Success(this.body() ?: DeleteLessonResponse())
                    401 -> LessonDetailState.Unauthorized
                    403 -> LessonDetailState.Forbidden
                    404 -> LessonDetailState.NotFound
                    else -> LessonDetailState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonDetailState.Error(Exception("Retrofit Exception"))
    }
}