package com.depromeet.sloth.ui.register

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.lesson.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.LessonRepository
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.data.network.member.MemberUpdateInfoRequest
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel(
    preferenceManager: PreferenceManager
): BaseViewModel() {
    private val lessonRepository = LessonRepository(preferenceManager)
    private val memberRepository = MemberRepository(preferenceManager)

    suspend fun fetchMemberInfo(accessToken: String) = viewModelScope.async {
        memberRepository.fetchMemberInfo(accessToken)
    }.await()

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

    suspend fun removeAuthToken(pm: PreferenceManager) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                pm.removeAuthToken()
            }
        }

}