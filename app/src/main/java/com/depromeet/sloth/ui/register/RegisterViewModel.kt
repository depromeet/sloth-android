package com.depromeet.sloth.ui.register

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.member.MemberInfoRepository
import com.depromeet.sloth.data.network.register.RegisterLessonRequest
import com.depromeet.sloth.data.network.register.RegisterRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.async

class RegisterViewModel: BaseViewModel() {
    private val registerRepository = RegisterRepository()
    private val memberInfoRepository = MemberInfoRepository()

    suspend fun fetchMemberInfo(accessToken: String) = viewModelScope.async {
        memberInfoRepository.fetchMemberInfo(accessToken)
    }.await()

    suspend fun registerNickname(
        accessToken: String,
        nickname: String
    ) = viewModelScope.async {
        registerRepository.registerNickname(accessToken, nickname)
    }.await()

    suspend fun registerLesson(
        accessToken: String,
        registerLessonRequest: RegisterLessonRequest
    ) = viewModelScope.async {
        registerRepository.registerLesson(accessToken, registerLessonRequest)
    }.await()
}