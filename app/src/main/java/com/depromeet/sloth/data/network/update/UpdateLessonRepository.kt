package com.depromeet.sloth.data.network.update

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.model.UpdateLessonModel
import com.depromeet.sloth.data.network.ServiceGenerator
import com.depromeet.sloth.data.network.register.RegisterLessonResponse
import com.depromeet.sloth.data.network.register.RegisterState

class UpdateLessonRepository {
    suspend fun updateLessonInfo(accessToken: String, lessonId: String, updateLessonModel: UpdateLessonModel): UpdateLessonState<UpdateLessonResponse> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(UpdateLessonService::class.java)
            .updateLessonInfo(
                lessonId,
                UpdateLessonRequest(
                    categoryId = updateLessonModel.categoryId,
                    lessonName = updateLessonModel.lessonName,
                    siteId = updateLessonModel.siteId,
                    totalNumber = updateLessonModel.totalNumber
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