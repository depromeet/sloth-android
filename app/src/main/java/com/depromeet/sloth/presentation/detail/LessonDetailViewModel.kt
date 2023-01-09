package com.depromeet.sloth.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonDetailResponse
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.use_case.lesson.DeleteLessonUseCase
import com.depromeet.sloth.domain.use_case.lesson.FetchLessonDetailUseCase
import com.depromeet.sloth.domain.use_case.member.DeleteAuthTokenUseCase
import com.depromeet.sloth.presentation.base.BaseViewModel
import com.depromeet.sloth.presentation.item.LessonDetail
import com.depromeet.sloth.util.INTERNET_CONNECTION_ERROR
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.util.UNAUTHORIZED
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
    private val fetchLessonDetailUseCase: FetchLessonDetailUseCase,
    private val deleteLessonUseCase: DeleteLessonUseCase,
    private val deleteAuthTokenUseCase: DeleteAuthTokenUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    val lessonId: String = checkNotNull(savedStateHandle[KEY_LESSON_ID])

    private val _deleteLessonSuccess = MutableSharedFlow<Unit>()
    val deleteLessonSuccess: SharedFlow<Unit> = _deleteLessonSuccess.asSharedFlow()

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
        fetchLessonDetailUseCase(lessonId)
            .onEach { result ->
                showLoading(result is Result.Loading)
            }.collect { result ->
                when(result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        internetError(false)
                        setLessonDetailInfo(result.data)
                    }
                    is Result.Error -> {
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            internetError(true)
                        }
                        else if (result.statusCode == UNAUTHORIZED) {
                            showForbiddenDialogEvent()
                        }
                        else {
                            showToastEvent(stringResourcesProvider.getString(R.string.lesson_fetch_fail))
                        }
                    }
                }
            }
    }

    fun deleteLesson() = viewModelScope.launch {
        deleteLessonUseCase(lessonId)
            .onEach { result ->
                showLoading(result is Result.Loading)
            }.collect {result ->
                when(result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        showToastEvent(stringResourcesProvider.getString(R.string.lesson_delete_complete))
                        _deleteLessonSuccess.emit(Unit)
                    }
                    is Result.Error -> {
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            showToastEvent(stringResourcesProvider.getString(R.string.lesson_delete_fail_by_internet))
                        }
                        else if (result.statusCode == UNAUTHORIZED) {
                            showForbiddenDialogEvent()
                        }
                        else {
                            showToastEvent(stringResourcesProvider.getString(R.string.lesson_delete_fail))
                        }
                    }
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

    fun deleteAuthToken() = viewModelScope.launch {
        deleteAuthTokenUseCase()
    }

    override fun retry() {
        fetchLessonDetail()
    }

    companion object {
        private const val KEY_LESSON_ID = "lessonId"
    }
}