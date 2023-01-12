package com.depromeet.sloth.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.use_case.lesson.DeleteLessonUseCase
import com.depromeet.sloth.domain.use_case.lesson.FetchLessonDetailUseCase
import com.depromeet.sloth.presentation.base.BaseViewModel
import com.depromeet.sloth.presentation.item.LessonDetail
import com.depromeet.sloth.util.INTERNET_CONNECTION_ERROR
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LessonDetailViewModel @Inject constructor(
    private val fetchLessonDetailUseCase: FetchLessonDetailUseCase,
    private val deleteLessonUseCase: DeleteLessonUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val lessonId: String = checkNotNull(savedStateHandle[KEY_LESSON_ID])

    private val _uiState = MutableStateFlow(LessonDetailUiState())
    val uiState: StateFlow<LessonDetailUiState> = _uiState.asStateFlow()

    private val _deleteLessonSuccessEvent = MutableSharedFlow<Unit>()
    val deleteLessonSuccessEvent: SharedFlow<Unit> = _deleteLessonSuccessEvent.asSharedFlow()

    private val _deleteLessonCancelEvent = MutableSharedFlow<Unit>()
    val deleteLessonCancelEvent: SharedFlow<Unit> = _deleteLessonCancelEvent.asSharedFlow()

    private val _navigateToUpdateLessonEvent = MutableSharedFlow<LessonDetail>()
    val navigateToUpdateLessonEvent: SharedFlow<LessonDetail> =
        _navigateToUpdateLessonEvent.asSharedFlow()

    private val _navigateToDeleteLessonDialogEvent = MutableSharedFlow<Unit>()
    val navigateToDeleteLessonDialogEvent: SharedFlow<Unit> =
        _navigateToDeleteLessonDialogEvent.asSharedFlow()

    fun fetchLessonDetail() = viewModelScope.launch {
        fetchLessonDetailUseCase(lessonId)
            .onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        setInternetError(false)
                        _uiState.update { lessonDetailUiState ->
                            lessonDetailUiState.copy(
                                lessonId = result.data.lessonId.toString(),
                                categoryName = result.data.categoryName,
                                currentProgressRate = result.data.currentProgressRate,
                                endDate = result.data.endDate,
                                goalProgressRate = result.data.goalProgressRate,
                                isFinished = result.data.isFinished,
                                lessonName = result.data.lessonName,
                                message = result.data.message,
                                presentNumber = result.data.presentNumber,
                                price = result.data.price,
                                remainDay = result.data.remainDay,
                                siteName = result.data.siteName,
                                startDate = result.data.startDate,
                                totalNumber = result.data.totalNumber,
                                wastePrice = result.data.wastePrice,
                            )
                        }
                    }

                    is Result.Error -> {
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            setInternetError(true)
                        } else if (result.statusCode == UNAUTHORIZED) {
                            navigateToExpireDialog()
                        } else {
                            showToast(stringResourcesProvider.getString(R.string.lesson_fetch_fail))
                        }
                    }
                }
            }
    }

    fun deleteLesson() = viewModelScope.launch {
        deleteLessonUseCase(lessonId)
            .onEach { result ->
                setLoading(result is Result.Loading)
            }
            .collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        Timber.d("emit")
                        showToast(stringResourcesProvider.getString(R.string.lesson_delete_complete))
                        _deleteLessonSuccessEvent.emit(Unit)
                    }

                    is Result.Error -> {
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            showToast(stringResourcesProvider.getString(R.string.lesson_delete_fail_by_internet))
                        } else if (result.statusCode == UNAUTHORIZED) {
                            navigateToExpireDialog()
                        } else {
                            showToast(stringResourcesProvider.getString(R.string.lesson_delete_fail))
                        }
                    }
                }
            }
    }

    fun navigateToUpdateLesson() = viewModelScope.launch {
        _navigateToUpdateLessonEvent.emit(
            LessonDetail(
                lessonId = uiState.value.lessonId,
                categoryName = uiState.value.categoryName,
                currentProgressRate = uiState.value.currentProgressRate,
                endDate = uiState.value.endDate,
                goalProgressRate = uiState.value.goalProgressRate,
                isFinished = uiState.value.isFinished,
                lessonName = uiState.value.lessonName,
                message = uiState.value.message,
                presentNumber = uiState.value.presentNumber,
                price = uiState.value.price,
                remainDay = uiState.value.remainDay,
                siteName = uiState.value.siteName,
                startDate =uiState.value.startDate,
                totalNumber = uiState.value.totalNumber,
                wastePrice = uiState.value.wastePrice,
            )
        )
    }

    fun navigateToDeleteLessonDialog() = viewModelScope.launch {
        _navigateToDeleteLessonDialogEvent.emit(Unit)
    }

    fun navigateToLessonDetail() = viewModelScope.launch {
        _deleteLessonCancelEvent.emit(Unit)
    }

    override fun retry() {
        fetchLessonDetail()
    }

    data class LessonDetailUiState(
        val lessonId: String = "",
        val categoryName: String = "",
        val currentProgressRate: Int = 0,
        val endDate: ArrayList<String> = ArrayList(),
        val goalProgressRate: Int = 0,
        val isFinished: Boolean = false,
        val lessonName: String = "",
        val message: String = "",
        val presentNumber: Int = 0,
        val price: Int = 0,
        val remainDay: Int = 0,
        val siteName: String = "",
        val startDate: ArrayList<String> = ArrayList(),
        val totalNumber: Int = 0,
        val wastePrice: Int = 0,
    )

    companion object {
        private const val KEY_LESSON_ID = "lesson_id"
    }
}