package com.depromeet.sloth.data.network.register

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.model.LessonModel
import com.depromeet.sloth.data.network.ServiceGenerator
import com.depromeet.sloth.data.network.member.MemberInfoState

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
                return when(this.code()) {
                    200 -> RegisterState.Success(this.body() ?: RegisterLessonResponse())
                    401 -> RegisterState.Unauthorized
                    403 -> RegisterState.Forbidden
                    404 -> RegisterState.NotFound
                    else -> RegisterState.Error(java.lang.Exception("Uncaught Exception"))
                }
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