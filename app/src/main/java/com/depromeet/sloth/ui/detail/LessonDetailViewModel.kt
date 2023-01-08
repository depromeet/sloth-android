package com.depromeet.sloth.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.response.lesson.LessonDetailResponse
import com.depromeet.sloth.domain.use_case.lesson.DeleteLessonUseCase
import com.depromeet.sloth.domain.use_case.lesson.GetLessonDetailUseCase
import com.depromeet.sloth.domain.use_case.member.RemoveAuthTokenUseCase
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.item.LessonDetail
import com.depromeet.sloth.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonDetailViewModel @Inject constructor(
    private val getLessonDetailUseCase: GetLessonDetailUseCase,
    private val deleteLessonUseCase: DeleteLessonUseCase,
    private val removeAuthTokenUseCase: RemoveAuthTokenUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    val lessonId: String = checkNotNull(savedStateHandle[KEY_LESSON_ID])

    private val _fetchLessonDetailSuccess = MutableSharedFlow<Unit>()
    val fetchLessonDetailSuccess: SharedFlow<Unit> = _fetchLessonDetailSuccess.asSharedFlow()

    private val _fetchLessonDetailFail = MutableSharedFlow<Int>()
    val fetchLessonDetailFail: SharedFlow<Int> = _fetchLessonDetailFail.asSharedFlow()

    private val _deleteLessonSuccess = MutableSharedFlow<Unit>()
    val deleteLessonSuccess: SharedFlow<Unit> = _deleteLessonSuccess.asSharedFlow()

    private val _deleteLessonFail = MutableSharedFlow<Int>()
    val deleteLessonFail: SharedFlow<Int> = _deleteLessonFail.asSharedFlow()

    init {
        fetchLessonDetail()
    }

    private val _lessonDetail = MutableStateFlow(LessonDetail())
    val lessonDetail: StateFlow<LessonDetail> = _lessonDetail.asStateFlow()

    private val _navigateToUpdateLessonEvent = MutableSharedFlow<LessonDetail>()
    val navigateToUpdateLessonEvent: SharedFlow<LessonDetail> = _navigateToUpdateLessonEvent.asSharedFlow()

    private val _navigateToDeleteLessonDialogEvent = MutableSharedFlow<Unit>()
    val navigateToDeleteLessonDialogEvent: SharedFlow<Unit> = _navigateToDeleteLessonDialogEvent.asSharedFlow()

    private fun fetchLessonDetail() = viewModelScope.launch {
        getLessonDetailUseCase(lessonId)
            .onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when(result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        _fetchLessonDetailSuccess.emit(Unit)
                        setLessonDetailInfo(result.data)
                    }
                    is Result.Error -> result.statusCode?.let { _fetchLessonDetailFail.emit(it) }
                }
            }
    }

    fun deleteLesson() = viewModelScope.launch {
        deleteLessonUseCase(lessonId)
            .onEach { result ->
                setLoading(result is Result.Loading)
            }.collect {result ->
                when(result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> _deleteLessonSuccess.emit(Unit)
                    is Result.Error -> result.statusCode?.let { _deleteLessonFail.emit(it) }
                }
            }
    }

    private fun setLessonDetailInfo(lessonDetailResponse: LessonDetailResponse) {
        _lessonDetail.value = LessonDetail (
            lessonId = lessonDetailResponse.lessonId.toString(),
            categoryName = lessonDetailResponse.categoryName,
            currentProgressRate= lessonDetailResponse.currentProgressRate,
            endDate = lessonDetailResponse.endDate,
            goalProgressRate = lessonDetailResponse.goalProgressRate,
            isFinished = lessonDetailResponse.isFinished,
            lessonName = lessonDetailResponse.lessonName,
            message = lessonDetailResponse.message,
            presentNumber= lessonDetailResponse.presentNumber,
            price = lessonDetailResponse.price,
            remainDay = lessonDetailResponse.remainDay,
            siteName = lessonDetailResponse.siteName,
            startDate = lessonDetailResponse.startDate,
            totalNumber = lessonDetailResponse.totalNumber,
            wastePrice = lessonDetailResponse.wastePrice,
        )
    }

    fun navigateToUpdateLesson(lessonDetail: LessonDetail) = viewModelScope.launch {
        _navigateToUpdateLessonEvent.emit(lessonDetail)
    }

    fun navigateToDeleteLessonDialog() = viewModelScope.launch {
        _navigateToDeleteLessonDialogEvent.emit(Unit)
    }

    fun removeAuthToken() = viewModelScope.launch {
        removeAuthTokenUseCase()
    }

    override fun retry() {
        fetchLessonDetail()
    }

    companion object {
        private const val KEY_LESSON_ID = "lessonId"
    }
}