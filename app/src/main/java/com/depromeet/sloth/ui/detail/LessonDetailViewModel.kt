package com.depromeet.sloth.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.LessonDetail
import com.depromeet.sloth.data.network.lesson.LessonRepository
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.data.network.lesson.LessonState
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonDetailViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    memberRepository: MemberRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(memberRepository) {

    val lessonId: String = savedStateHandle.get(LessonDetailActivity.LESSON_ID)
        ?: throw IllegalStateException("There is no value of the lesson id.")

    private val _lessonDetailState = MutableLiveData<LessonState<LessonDetail>>()
    val lessonDetailState: LiveData<LessonState<LessonDetail>> = _lessonDetailState

    private val _lessonDeleteState = MutableLiveData<LessonState<LessonDeleteResponse>>()
    val lessonDeleteState: LiveData<LessonState<LessonDeleteResponse>> = _lessonDeleteState

    private val _lessonDetail = MutableLiveData<LessonDetail>()
    val lessonDetail: LiveData<LessonDetail> = _lessonDetail

    private val _lessonUpdateEvent = MutableLiveData<Event<LessonDetail>>()
    val lessonUpdateEvent: LiveData<Event<LessonDetail>> = _lessonUpdateEvent

    private val _lessonDeleteEvent = MutableLiveData<Event<Boolean>>()
    val lessonDeleteEvent: LiveData<Event<Boolean>> = _lessonDeleteEvent


    fun fetchLessonDetail() = viewModelScope.launch {
        _lessonDetailState.value = LessonState.Loading
        val lessonDetail = lessonRepository.fetchLessonDetail(lessonId)
        _lessonDetailState.value = lessonDetail
    }


    fun deleteLesson() = viewModelScope.launch {
        _lessonDeleteState.value = LessonState.Loading
        val lessonDeleteResponse = lessonRepository.deleteLesson(lessonId)
        _lessonDeleteState.value = lessonDeleteResponse
    }

    fun setLessonDetailInfo(lessonDetail: LessonDetail) {
        _lessonDetail.value = lessonDetail
    }

    fun onClickLessonUpdateEvent(lessonDetail: LessonDetail) {
        _lessonUpdateEvent.value = Event(lessonDetail)
    }

    fun onClickLessonDeleteEvent() {
        _lessonDeleteEvent.value = Event(true)
    }
}