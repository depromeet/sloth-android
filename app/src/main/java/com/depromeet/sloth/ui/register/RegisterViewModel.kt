package com.depromeet.sloth.ui.register

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.data.network.member.MemberUpdateInfoRequest
import com.depromeet.sloth.data.network.register.RegisterLessonRequest
import com.depromeet.sloth.data.network.register.RegisterRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.async

class RegisterViewModel: BaseViewModel() {
    private val registerRepository = RegisterRepository()
    private val memberRepository = MemberRepository()

    suspend fun fetchMemberInfo(accessToken: String) = viewModelScope.async {
        memberRepository.fetchMemberInfo(accessToken)
    }.await()

    suspend fun updateMemberInfo(
        accessToken: String,
        memberUpdateInfoRequest: MemberUpdateInfoRequest
    ) = viewModelScope.async {
        memberRepository.updateMemberInfo(accessToken, memberUpdateInfoRequest)
    }.await()

    suspend fun registerLesson(
        accessToken: String,
        registerLessonRequest: RegisterLessonRequest
    ) = viewModelScope.async {
        registerRepository.registerLesson(accessToken, registerLessonRequest)
    }.await()
}