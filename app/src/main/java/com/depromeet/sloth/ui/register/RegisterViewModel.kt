package com.depromeet.sloth.ui.register

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.lesson.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.LessonRepository
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.data.network.member.MemberUpdateInfoRequest
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.withContext

class RegisterViewModel @ViewModelInject constructor(
    private val lessonRepository: LessonRepository,
    private val memberRepository: MemberRepository
): BaseViewModel() {
    suspend fun fetchMemberInfo(accessToken: String) =
        withContext(viewModelScope.coroutineContext) {
            memberRepository.fetchMemberInfo(accessToken)
        }

    suspend fun updateMemberInfo(
        accessToken: String,
        memberUpdateInfoRequest: MemberUpdateInfoRequest
    ) = withContext(viewModelScope.coroutineContext) {
        memberRepository.updateMemberInfo(accessToken, memberUpdateInfoRequest)
    }

    suspend fun registerLesson(
        accessToken: String,
        request: LessonRegisterRequest
    ) = withContext(viewModelScope.coroutineContext) {
        lessonRepository.registerLesson(accessToken, request)
    }

    suspend fun fetchLessonCategoryList(accessToken: String) =
        withContext(viewModelScope.coroutineContext) {
            lessonRepository.fetchLessonCategoryList(accessToken)
        }

    suspend fun fetchLessonSiteList(accessToken: String) =
        withContext(viewModelScope.coroutineContext) {
            lessonRepository.fetchLessonSiteList(accessToken)
        }
}