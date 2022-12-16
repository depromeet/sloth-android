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

    val lessonId: String = checkNotNull(savedStateHandle[KEY_LESSON_ID])

    private val _fetchLessonDetailEvent = MutableSharedFlow<Result<LessonDetailResponse>>()
    val fetchLessonDetailEvent: SharedFlow<Result<LessonDetailResponse>> = _fetchLessonDetailEvent.asSharedFlow()

    private val _deleteLessonEvent = MutableSharedFlow<Result<LessonDeleteResponse>>()
    val deleteLessonEvent: SharedFlow<Result<LessonDeleteResponse>> =
        _deleteLessonEvent.asSharedFlow()

    private val _lessonDetail = MutableStateFlow(LessonDetailResponse())
    val lessonDetail: StateFlow<LessonDetailResponse> = _lessonDetail.asStateFlow()

    private val _navigateToUpdateLessonEvent = MutableSharedFlow<LessonDetailResponse>()
    val navigateToUpdateLessonEvent: SharedFlow<LessonDetailResponse> = _navigateToUpdateLessonEvent.asSharedFlow()

    private val _showDeleteLessonDialogEvent = MutableSharedFlow<Unit>()
    val showDeleteLessonDialogEvent: SharedFlow<Unit> = _showDeleteLessonDialogEvent.asSharedFlow()

    fun fetchLessonDetail() = viewModelScope.launch {
        getLessonDetailUseCase(lessonId)
            .onEach {
                if (it is Result.Loading) _fetchLessonDetailEvent.emit(Result.Loading)
                else _fetchLessonDetailEvent.emit(Result.UnLoading)
            }.collect {
                _fetchLessonDetailEvent.emit(it)
            }
    }

    fun deleteLesson() = viewModelScope.launch {
        deleteLessonUseCase(lessonId)
            .onEach {
                if (it is Result.Loading) _deleteLessonEvent.emit(Result.Loading)
                else _deleteLessonEvent.emit(Result.UnLoading)
            }.collect {
                _deleteLessonEvent.emit(it)
            }
    }

    fun setLessonDetailInfo(lessonDetailResponse: LessonDetailResponse) {
        _lessonDetail.value = lessonDetailResponse
    }

    fun navigateToUpdateLesson(lessonDetailResponse: LessonDetailResponse) = viewModelScope.launch {
        _navigateToUpdateLessonEvent.emit(lessonDetailResponse)
    }

    fun showDeleteLessonDialog() = viewModelScope.launch {
        _showDeleteLessonDialogEvent.emit(Unit)
    }

    fun removeAuthToken() = viewModelScope.launch {
        removeAuthTokenUseCase()
    }

    companion object {
        private const val KEY_LESSON_ID = "lessonId"
    }
}