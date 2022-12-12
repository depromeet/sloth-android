package com.depromeet.sloth.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.data.model.response.lesson.LessonDeleteResponse
import com.depromeet.sloth.data.model.response.lesson.LessonDetailResponse
import com.depromeet.sloth.domain.use_case.lesson.DeleteLessonUseCase
import com.depromeet.sloth.domain.use_case.lesson.GetLessonDetailUseCase
import com.depromeet.sloth.domain.use_case.member.RemoveAuthTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonDetailViewModel @Inject constructor(
    private val getLessonDetailUseCase: GetLessonDetailUseCase,
    private val deleteLessonUseCase: DeleteLessonUseCase,
    private val removeAuthTokenUseCase: RemoveAuthTokenUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

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

    fun fetchLessonDetail() = viewModelScope.launch {
        getLessonDetailUseCase(lessonId)
            .onEach {
                if (it is Result.Loading) _lessonDetailState.emit(Result.Loading)
                else _lessonDetailState.emit(Result.UnLoading)
            }.collect {
                _lessonDetailState.emit(it)
            }
    }

    fun deleteLesson() = viewModelScope.launch {
        deleteLessonUseCase(lessonId)
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

    fun removeAuthToken() = viewModelScope.launch {
        removeAuthTokenUseCase()
    }

    companion object {
        private const val LESSON_ID = "lessonId"
    }
}