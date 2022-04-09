package com.depromeet.sloth.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.lesson.*
import com.depromeet.sloth.data.network.lesson.category.LessonCategoryResponse
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteState
import com.depromeet.sloth.data.network.lesson.detail.LessonDetailResponse
import com.depromeet.sloth.data.network.lesson.detail.LessonDetailState
import com.depromeet.sloth.data.network.lesson.list.LessonState
import com.depromeet.sloth.data.network.lesson.site.LessonSiteResponse
import com.depromeet.sloth.data.network.member.MemberLogoutState
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.ui.Event
import com.depromeet.sloth.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LessonDetailViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    private val memberRepository: MemberRepository,
) : BaseViewModel() {

    private val _lessonDetailState = MutableLiveData<LessonDetailState<LessonDetailResponse>>()
    val lessonDetailState: LiveData<LessonDetailState<LessonDetailResponse>> = _lessonDetailState


    private val _lessonDeleteState = MutableLiveData<LessonDeleteState<LessonDeleteResponse>>()
    val lessonDeleteState: LiveData<LessonDeleteState<LessonDeleteResponse>> = _lessonDeleteState

    private val _memberLogoutState = MutableLiveData<MemberLogoutState<String>>()
    val memberLogoutState: LiveData<MemberLogoutState<String>> = _memberLogoutState


    suspend fun fetchLessonDetail(lessonId: String) =
        withContext(viewModelScope.coroutineContext) {
            lessonRepository.fetchLessonDetail(lessonId)
        }

    fun deleteLesson(lessonId: String) = viewModelScope.launch {
        _lessonDeleteState.value = LessonDeleteState.Loading
        val lessonDeleteResponse = lessonRepository.deleteLesson(lessonId)
        _lessonDeleteState.value = lessonDeleteResponse
    }


    fun logout() = viewModelScope.launch {
        _memberLogoutState.value = MemberLogoutState.Loading
        val memberLogoutState = memberRepository.logout()
        _memberLogoutState.value = memberLogoutState
    }

    fun removeAuthToken() = viewModelScope.launch {
        memberRepository.removeAuthToken()
    }
}