package com.depromeet.sloth.data.network.update

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.db.model.UpdateLessonModel
import com.depromeet.sloth.data.network.ServiceGenerator

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
                return UpdateLessonState.Success(
                    this.body() ?: UpdateLessonResponse()
                )
            } ?: return UpdateLessonState.Error(Exception("Retrofit Exception"))
    }
}