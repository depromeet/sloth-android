package com.depromeet.sloth.ui.register

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.data.network.member.MemberUpdateInfoRequest
import com.depromeet.sloth.data.network.lesson.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.LessonRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel: BaseViewModel() {
    private val lessonRepository = LessonRepository()
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
        request: LessonRegisterRequest
    ) = viewModelScope.async {
        lessonRepository.registerLesson(accessToken, request)
    }.await()

    suspend fun fetchLessonCategoryList(accessToken: String) = viewModelScope.async {
        lessonRepository.fetchLessonCategoryList(accessToken)
    }.await()

    suspend fun fetchLessonSiteList(accessToken: String) = viewModelScope.async {
        lessonRepository.fetchLessonSiteList(accessToken)
    }.await()

    suspend fun removeAuthToken(pm: PreferenceManager) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                pm.removeAuthToken()
            }
        }

}