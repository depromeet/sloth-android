package com.depromeet.sloth.ui.update

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.lesson.LessonRepository
import com.depromeet.sloth.data.network.lesson.category.LessonCategoryResponse
import com.depromeet.sloth.data.network.lesson.list.LessonState
import com.depromeet.sloth.data.network.lesson.site.LessonSiteResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateRequest
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateState
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateLessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    private val memberRepository: MemberRepository,
) : BaseViewModel() {

    private val _lessonUpdateState = MutableLiveData<LessonUpdateState<LessonUpdateResponse>>()
    val lessonUpdateState: LiveData<LessonUpdateState<LessonUpdateResponse>> = _lessonUpdateState

    private val _lessonCategoryState = MutableLiveData<LessonState<List<LessonCategoryResponse>>>()
    val lessonCategoryState: LiveData<LessonState<List<LessonCategoryResponse>>> =
        _lessonCategoryState

    private val _lessonSiteState = MutableLiveData<LessonState<List<LessonSiteResponse>>>()
    val lessonSiteState: LiveData<LessonState<List<LessonSiteResponse>>> = _lessonSiteState

    init {
        viewModelScope.launch {
            fetchLessonCategoryList()
            fetchLessonSiteList()
        }
    }

    fun updateLesson(
        lessonId: String,
        updateLessonRequest: LessonUpdateRequest,
    ) = viewModelScope.launch {
        _lessonUpdateState.value = LessonUpdateState.Loading
        val lessonUpdateResponse = lessonRepository.updateLesson(lessonId, updateLessonRequest)
        _lessonUpdateState.value = lessonUpdateResponse
    }

    private suspend fun fetchLessonCategoryList() {
        Log.d("UpdateLessonViewModel", "fetchLessonCategoryList: 호출")
        _lessonCategoryState.value = LessonState.Loading
        val lessonCategoryResponse = lessonRepository.fetchLessonCategoryList()
        _lessonCategoryState.value = lessonCategoryResponse
        Log.d("UpdateLessonViewModel", "fetchLessonCategoryList: 호출 완료")
    }

    private suspend fun fetchLessonSiteList() {
        Log.d("UpdateLessonViewModel", "fetchLessonSiteList: 호출")
        _lessonSiteState.value = LessonState.Loading
        val lessonSiteResponse = lessonRepository.fetchLessonSiteList()
        _lessonSiteState.value = lessonSiteResponse
        Log.d("UpdateLessonViewModel", "fetchLessonSiteList: 호출 완료")
    }

    fun removeAuthToken() = viewModelScope.launch {
        memberRepository.removeAuthToken()
    }
}