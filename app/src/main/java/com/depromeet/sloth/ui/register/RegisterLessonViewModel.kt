package com.depromeet.sloth.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.Lesson
import com.depromeet.sloth.data.network.lesson.LessonRepository
import com.depromeet.sloth.data.network.lesson.category.LessonCategoryResponse
import com.depromeet.sloth.data.network.lesson.list.LessonState
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterResponse
import com.depromeet.sloth.data.network.lesson.site.LessonSiteResponse
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterLessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    memberRepository: MemberRepository,
) : BaseViewModel(memberRepository) {

    private val _lessonCategoryListState =
        MutableLiveData<Event<LessonState<List<LessonCategoryResponse>>>>()
    val lessonCategoryListState: LiveData<Event<LessonState<List<LessonCategoryResponse>>>> =
        _lessonCategoryListState

    private val _lessonSiteListState =
        MutableLiveData<Event<LessonState<List<LessonSiteResponse>>>>()
    val lessonSiteListState: LiveData<Event<LessonState<List<LessonSiteResponse>>>> =
        _lessonSiteListState

    private val _lessonRegisterState = MutableLiveData<Event<LessonState<LessonRegisterResponse>>>()
    val lessonRegisterState: LiveData<Event<LessonState<LessonRegisterResponse>>> =
        _lessonRegisterState

    private val _lessonRegister = MutableLiveData<Lesson>()
    val lessonRegister: LiveData<Lesson> = _lessonRegister

    private val _lessonCategorySelectedItemPosition = MutableLiveData<Int>()
    val lessonCategorySelectedItemPosition: LiveData<Int> = _lessonCategorySelectedItemPosition

    private val _lessonSiteSelectedItemPosition = MutableLiveData<Int>()
    val lessonSiteSelectedItemPosition: LiveData<Int> = _lessonSiteSelectedItemPosition

    val lessonName = MutableLiveData<String>()

    private val _lessonTotalNumber = MutableLiveData<Int>()
    val lessonTotalNumber: LiveData<Int> = _lessonTotalNumber

    private val _lesson = MutableLiveData<Lesson>()
    val lesson: LiveData<Lesson> = _lesson

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
        _lessonRegisterState.value = Event(LessonState.Loading)
        val lessonRegisterResponse = lessonRepository.registerLesson(request)
        _lessonRegisterState.value = Event(lessonRegisterResponse)
    }

    private suspend fun fetchLessonCategoryList() {
        _lessonCategoryListState.value = Event(LessonState.Loading)
        val lessonCategoryListResponse = lessonRepository.fetchLessonCategoryList()
        _lessonCategoryListState.value = Event(lessonCategoryListResponse)
    }

    private suspend fun fetchLessonSiteList() {
        _lessonSiteListState.value = Event(LessonState.Loading)
        val lessonSiteListResponse = lessonRepository.fetchLessonSiteList()
        _lessonSiteListState.value = Event(lessonSiteListResponse)
    }

    fun setLessonCategoryItemPosition(position: Int) {
        _lessonCategorySelectedItemPosition.value = position
    }

    fun setLessonSiteItemPosition(position: Int) {
        _lessonSiteSelectedItemPosition.value = position
    }
}