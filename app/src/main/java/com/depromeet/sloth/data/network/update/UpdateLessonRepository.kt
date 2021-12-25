package com.depromeet.sloth.data.network.update

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.network.ServiceGenerator

class UpdateLessonRepository {
    suspend fun updateLessonInfo(
        accessToken: String,
        lessonId: String,
        updateLessonRequest: UpdateLessonRequest
    ): UpdateLessonState<UpdateLessonResponse> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(UpdateLessonService::class.java)
            .updateLessonInfo(
                lessonId,
                UpdateLessonRequest(
                    categoryId = updateLessonRequest.categoryId,
                    lessonName = updateLessonRequest.lessonName,
                    siteId = updateLessonRequest.siteId,
                    totalNumber = updateLessonRequest.totalNumber
                )
            )?.run {
                    return when(this.code()) {
                        200 -> UpdateLessonState.Success(this.body() ?: UpdateLessonResponse())
                        204 -> UpdateLessonState.NoContent
                        401 -> UpdateLessonState.Unauthorized
                        403 -> UpdateLessonState.Forbidden
                        else -> UpdateLessonState.Error(Exception("Uncaught Exception"))
                    }
            } ?: return UpdateLessonState.Error(Exception("Retrofit Exception"))
    }
}