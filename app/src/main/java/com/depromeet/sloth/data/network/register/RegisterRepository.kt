package com.depromeet.sloth.data.network.register

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.model.LessonModel
import com.depromeet.sloth.data.network.ServiceGenerator

class RegisterRepository {
    suspend fun registerLesson(
        accessToken: String,
        lessonModel: LessonModel
    ): RegisterState<RegisterLessonResponse> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(RegisterService::class.java)
            .registerLesson(
                RegisterLessonRequest(
                    alertDays = lessonModel.alertDays,
                    categoryId = lessonModel.categoryId,
                    endDate = lessonModel.endDate,
                    lessonName = lessonModel.lessonName,
                    message = lessonModel.message,
                    price = lessonModel.price,
                    siteId = lessonModel.siteId,
                    startDate = lessonModel.startDate,
                    totalNumber = lessonModel.totalNumber
                )
            )?.run {
                return RegisterState.Success(
                    this.body() ?: RegisterLessonResponse()
                )
            } ?: return RegisterState.Error(Exception("Register Exception"))
    }

    suspend fun registerNickname(
        accessToken: String,
        nickname: String
    ): RegisterState<Int> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(RegisterService::class.java)
            .registerNickname(
                RegisterNicknameRequest(
                    memberName = nickname
                )
            )?.run {
                return RegisterState.Success(
                    0
                )
            } ?: return RegisterState.Error(Exception("Register Exception"))
    }
}