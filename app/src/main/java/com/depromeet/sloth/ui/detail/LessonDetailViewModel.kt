package com.depromeet.sloth.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.LessonDetail
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.data.repository.LessonRepository
import com.depromeet.sloth.data.repository.MemberRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.common.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonDetailViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    savedStateHandle: SavedStateHandle,
    memberRepository: MemberRepository,
) : BaseViewModel(memberRepository) {

    val lessonId: String = checkNotNull(savedStateHandle[LESSON_ID])

    private val _lessonDetailState = MutableSharedFlow<Result<LessonDetail>>()
    val lessonDetailState: SharedFlow<Result<LessonDetail>> = _lessonDetailState.asSharedFlow()

    private val _lessonDeleteState = MutableSharedFlow<Result<LessonDeleteResponse>>()
    val lessonDeleteState: SharedFlow<Result<LessonDeleteResponse>> =
        _lessonDeleteState.asSharedFlow()

    private val _lessonDetail = MutableStateFlow(LessonDetail())
    val lessonDetail: StateFlow<LessonDetail> = _lessonDetail.asStateFlow()

    private val _lessonUpdateClick = MutableSharedFlow<LessonDetail>()
    val lessonUpdateClick: SharedFlow<LessonDetail>
        get() = _lessonUpdateClick

    private val _lessonDeleteClick = MutableSharedFlow<Unit>()
    val lessonDeleteClick: SharedFlow<Unit>
        get() = _lessonDeleteClick

    fun fetchLessonDetail() = viewModelScope.launch {
        _lessonDetailState.emit(Result.Loading)
        _lessonDetailState.emit(lessonRepository.fetchLessonDetail(lessonId))
    }

    fun deleteLesson() = viewModelScope.launch {
        _lessonDeleteState.emit(Result.Loading)
        _lessonDeleteState.emit(lessonRepository.deleteLesson(lessonId))
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

    companion object {
        private const val LESSON_ID = "lessonId"
    }
}