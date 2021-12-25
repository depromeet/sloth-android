package com.depromeet.sloth.data.network.register

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.network.ServiceGenerator

class RegisterRepository {
    suspend fun registerLesson(
        accessToken: String,
        registerLessonRequest: RegisterLessonRequest
    ): RegisterState<RegisterLessonResponse> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(RegisterService::class.java)
            .registerLesson(
                RegisterLessonRequest(
                    alertDays = registerLessonRequest.alertDays,
                    categoryId = registerLessonRequest.categoryId,
                    endDate = registerLessonRequest.endDate,
                    lessonName = registerLessonRequest.lessonName,
                    message = registerLessonRequest.message,
                    price = registerLessonRequest.price,
                    siteId = registerLessonRequest.siteId,
                    startDate = registerLessonRequest.startDate,
                    totalNumber = registerLessonRequest.totalNumber
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