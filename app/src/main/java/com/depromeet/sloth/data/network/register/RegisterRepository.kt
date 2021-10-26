package com.depromeet.sloth.data.network.register

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.model.LessonModel
import com.depromeet.sloth.data.network.ServiceGenerator
import com.depromeet.sloth.data.network.ServiceGenerator.createService
import com.depromeet.sloth.data.network.login.*

class RegisterRepository {

    suspend fun registerLesson(lessonModel: LessonModel): RegisterState<RegisterLessonResponse> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL
        ).createService(
            serviceClass = RegisterService::class.java,
        ).registerLesson(
            RegisterLessonRequest(
                alertDays = lessonModel.alertDays,
                categoryId = lessonModel.categoryId,
                endDate = lessonModel.endDate,
                lessonName = lessonModel.lessonName,
                memberId = lessonModel.memberId,
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

    suspend fun registerNickname(accessToken: String, nickname:String):RegisterState<Int> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        ).createService(
            serviceClass = RegisterService::class.java,
        ).registerNickname(
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