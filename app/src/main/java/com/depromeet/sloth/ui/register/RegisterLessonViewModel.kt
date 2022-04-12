package com.depromeet.sloth.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.Lesson
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.LessonRepository
import com.depromeet.sloth.data.network.lesson.category.LessonCategoryResponse
import com.depromeet.sloth.data.network.lesson.list.LessonState
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterResponse
import com.depromeet.sloth.data.network.lesson.site.LessonSiteResponse
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterLessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    memberRepository: MemberRepository,
) : BaseViewModel(memberRepository) {

    private val _lessonCategoryListState =
        MutableLiveData<LessonState<List<LessonCategoryResponse>>>()
    val lessonCategoryListState: LiveData<LessonState<List<LessonCategoryResponse>>> =
        _lessonCategoryListState

    private val _lessonSiteListState = MutableLiveData<LessonState<List<LessonSiteResponse>>>()
    val lessonSiteListState: LiveData<LessonState<List<LessonSiteResponse>>> = _lessonSiteListState

    private val _lessonRegisterState = MutableLiveData<LessonState<LessonRegisterResponse>>()
    val lessonRegisterState: LiveData<LessonState<LessonRegisterResponse>> = _lessonRegisterState

    private val _lessonRegister = MutableLiveData<Lesson>()
    val lessonRegister: LiveData<Lesson> = _lessonRegister

    init {
        viewModelScope.launch {
            fetchLessonCategoryList()
            fetchLessonSiteList()
        }
    }

    fun setLessonRegisterInfo(lesson: Lesson) = with(lesson) {
        _lessonRegister.value = Lesson(
            alertDays,
            categoryName,
            endDate,
            lessonName,
            message,
            price,
            siteName,
            startDate,
            totalNumber
        )
    }

    fun registerLesson(
        request: LessonRegisterRequest,
    ) = viewModelScope.launch {
        _lessonRegisterState.value = LessonState.Loading
        val lessonRegisterResponse = lessonRepository.registerLesson(request)
        _lessonRegisterState.value = lessonRegisterResponse
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
}