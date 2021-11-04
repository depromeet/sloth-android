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
                return LessonDetailState.Success(
                    this.body() ?: LessonDetailResponse()
                )
            } ?: return LessonDetailState.Error(Exception("Retrofit Exception"))
    }
}