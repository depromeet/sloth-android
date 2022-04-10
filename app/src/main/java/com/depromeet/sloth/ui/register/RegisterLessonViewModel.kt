package com.depromeet.sloth.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.LessonRepository
import com.depromeet.sloth.data.network.lesson.category.LessonCategoryResponse
import com.depromeet.sloth.data.network.lesson.list.LessonState
import com.depromeet.sloth.data.network.lesson.site.LessonSiteResponse
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegisterLessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    private val memberRepository: MemberRepository
) : BaseViewModel() {

    private val _lessonCategoryListState = MutableLiveData<LessonState<List<LessonCategoryResponse>>>()
    val lessonCategoryListState: LiveData<LessonState<List<LessonCategoryResponse>>> =
        _lessonCategoryListState

    private val _lessonSiteListState = MutableLiveData<LessonState<List<LessonSiteResponse>>>()
    val lessonSiteListState: LiveData<LessonState<List<LessonSiteResponse>>> = _lessonSiteListState

    init {
        viewModelScope.launch {
            fetchLessonCategoryList()
            fetchLessonSiteList()
        }
    }

    suspend fun registerLesson (
        request: LessonRegisterRequest,
    ) = withContext(viewModelScope.coroutineContext) {
        lessonRepository.registerLesson(request)
    }

    private suspend fun fetchLessonCategoryList() {
        _lessonCategoryListState.value = LessonState.Loading
        val lessonCategoryListResponse = lessonRepository.fetchLessonCategoryList()
        _lessonCategoryListState.value = lessonCategoryListResponse
    }

    private suspend fun fetchLessonSiteList() {
        _lessonSiteListState.value = LessonState.Loading
        val lessonSiteListResponse = lessonRepository.fetchLessonSiteList()
        _lessonSiteListState.value = lessonSiteListResponse
    }

    fun removeAuthToken() = viewModelScope.launch {
        memberRepository.removeAuthToken()
    }
}