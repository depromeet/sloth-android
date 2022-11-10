package com.depromeet.sloth.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.response.lesson.LessonDetailResponse
import com.depromeet.sloth.data.model.response.lesson.LessonDeleteResponse
import com.depromeet.sloth.data.repository.LessonRepository
import com.depromeet.sloth.data.repository.MemberRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.common.Result
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

    private val _lessonDetailState = MutableSharedFlow<Result<LessonDetailResponse>>()
    val lessonDetailState: SharedFlow<Result<LessonDetailResponse>> = _lessonDetailState.asSharedFlow()

    private val _lessonDeleteState = MutableSharedFlow<Result<LessonDeleteResponse>>()
    val lessonDeleteState: SharedFlow<Result<LessonDeleteResponse>> =
        _lessonDeleteState.asSharedFlow()

    private val _lessonDetail = MutableStateFlow(LessonDetailResponse())
    val lessonDetail: StateFlow<LessonDetailResponse> = _lessonDetail.asStateFlow()

    private val _lessonUpdateClick = MutableSharedFlow<LessonDetailResponse>()
    val lessonUpdateClick: SharedFlow<LessonDetailResponse>
        get() = _lessonUpdateClick

    private val _lessonDeleteClick = MutableSharedFlow<Unit>()
    val lessonDeleteClick: SharedFlow<Unit>
        get() = _lessonDeleteClick

//    fun fetchLessonDetail() = viewModelScope.launch {
//        _lessonDetailResponseState.emit(Result.Loading)
//        _lessonDetailResponseState.emit(lessonRepository.fetchLessonDetail(lessonId))
//    }
    fun fetchLessonDetail() = viewModelScope.launch {
        lessonRepository.fetchLessonDetail(lessonId)
            .onEach {
                if (it is Result.Loading) _lessonDetailState.emit(Result.Loading)
                else _lessonDetailState.emit(Result.UnLoading)
            }.collect {
                _lessonDetailState.emit(it)
            }
    }

//    fun deleteLesson() = viewModelScope.launch {
//        _lessonDeleteState.emit(Result.Loading)
//        _lessonDeleteState.emit(lessonRepository.deleteLesson(lessonId))
//    }
    fun deleteLesson() = viewModelScope.launch {
        lessonRepository.deleteLesson(lessonId)
            .onEach {
                if (it is Result.Loading) _lessonDeleteState.emit(Result.Loading)
                else _lessonDeleteState.emit(Result.UnLoading)
            }.collect {
                _lessonDeleteState.emit(it)
            }
    }

    fun setLessonDetailInfo(lessonDetailResponse: LessonDetailResponse) {
        _lessonDetail.value = lessonDetailResponse
    }

    fun lessonUpdateClick(lessonDetailResponse: LessonDetailResponse) = viewModelScope.launch {
        _lessonUpdateClick.emit(lessonDetailResponse)
    }

    fun lessonDeleteClick() = viewModelScope.launch {
        _lessonDeleteClick.emit(Unit)
    }

    companion object {
        private const val LESSON_ID = "lessonId"
    }
}