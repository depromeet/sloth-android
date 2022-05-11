package com.depromeet.sloth.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.LessonDetail
import com.depromeet.sloth.data.network.lesson.*
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteState
import com.depromeet.sloth.data.network.lesson.detail.LessonDetailResponse
import com.depromeet.sloth.data.network.lesson.detail.LessonDetailState
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
) : BaseViewModel(memberRepository) {

    private val _lessonDetailState = MutableLiveData<LessonDetailState<LessonDetailResponse>>()
    val lessonDetailState: LiveData<LessonDetailState<LessonDetailResponse>> = _lessonDetailState

    private val _lessonDeleteState = MutableLiveData<LessonDeleteState<LessonDeleteResponse>>()
    val lessonDeleteState: LiveData<LessonDeleteState<LessonDeleteResponse>> = _lessonDeleteState

    private val _lessonDetail = MutableLiveData<LessonDetail>()
    val lessonDetail: LiveData<LessonDetail> = _lessonDetail

    private val _lessonUpdateEvent = MutableLiveData<Event<LessonDetail>>()
    val lessonUpdateEvent: LiveData<Event<LessonDetail>> = _lessonUpdateEvent

    private val _lessonDeleteEvent = MutableLiveData<Event<Boolean>>()
    val lessonDeleteEvent: LiveData<Event<Boolean>> = _lessonDeleteEvent

    fun fetchLessonDetail(lessonId: String) = viewModelScope.launch {
        _lessonDetailState.value = LessonDetailState.Loading
        val lessonDetailResponse = lessonRepository.fetchLessonDetail(lessonId)
        _lessonDetailState.value = lessonDetailResponse
    }

    fun deleteLesson(lessonId: String) = viewModelScope.launch {
        _lessonDeleteState.value = LessonDeleteState.Loading
        val lessonDeleteResponse = lessonRepository.deleteLesson(lessonId)
        _lessonDeleteState.value = lessonDeleteResponse
    }

    fun setLessonDetailInfo(lessonDetailResponse: LessonDetailResponse) =
        with(lessonDetailResponse) {
            _lessonDetail.value = LessonDetail(
                alertDays,
                categoryName,
                currentProgressRate.toFloat(),
                endDate,
                goalProgressRate.toFloat(),
                isFinished,
                lessonId,
                lessonName,
                message,
                presentNumber,
                price,
                remainDay,
                siteName,
                startDate,
                totalNumber,
                wastePrice
            )
        }

    fun onClickLessonUpdateEvent(lessonDetail: LessonDetail) {
        _lessonUpdateEvent.value = Event(lessonDetail)
    }

    fun onClickLessonDeleteEvent() {
        _lessonDeleteEvent.value = Event(true)
    }
}