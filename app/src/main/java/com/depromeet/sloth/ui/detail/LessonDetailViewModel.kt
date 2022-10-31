package com.depromeet.sloth.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.LessonDetail
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.data.repository.LessonRepository
import com.depromeet.sloth.data.repository.MemberRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonDetailViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    memberRepository: MemberRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(memberRepository) {

    val lessonId: String = checkNotNull(savedStateHandle[LessonDetailActivity.LESSON_ID])

    private val _lessonDetailState = MutableLiveData<UiState<LessonDetail>>()
    val lessonDetailState: LiveData<UiState<LessonDetail>>
        get() = _lessonDetailState

    private val _lessonDeleteState = MutableLiveData<UiState<LessonDeleteResponse>>()
    val lessonDeleteState: LiveData<UiState<LessonDeleteResponse>>
        get() = _lessonDeleteState

    private val _lessonDetail = MutableLiveData<LessonDetail>()
    val lessonDetail: LiveData<LessonDetail>
        get() = _lessonDetail

    private val _lessonUpdateClick = MutableSharedFlow<LessonDetail>()
    val lessonUpdateClick: SharedFlow<LessonDetail>
        get() = _lessonUpdateClick

    private val _lessonDeleteClick = MutableSharedFlow<Unit>()
    val lessonDeleteClick: SharedFlow<Unit>
        get() = _lessonDeleteClick

    fun fetchLessonDetail() = viewModelScope.launch {
        _lessonDetailState.value = UiState.Loading
        _lessonDetailState.value = lessonRepository.fetchLessonDetail(lessonId)
    }

    fun deleteLesson() = viewModelScope.launch {
        _lessonDeleteState.value = UiState.Loading
        _lessonDeleteState.value = lessonRepository.deleteLesson(lessonId)
    }

    fun setLessonDetailInfo(lessonDetail: LessonDetail) {
        _lessonDetail.value = lessonDetail
    }

    fun lessonUpdateClick(lessonDetail: LessonDetail) = viewModelScope.launch {
        _lessonUpdateClick.emit(lessonDetail)
    }

    fun lessonDeleteClick() = viewModelScope.launch {
        _lessonDeleteClick.emit(Unit)
    }
}