package com.depromeet.presentation.ui.lessondetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.domain.usecase.lesson.FetchLessonDetailUseCase
import com.depromeet.domain.util.Result
import com.depromeet.presentation.R
import com.depromeet.presentation.di.StringResourcesProvider
import com.depromeet.presentation.model.LessonDetail
import com.depromeet.presentation.ui.base.BaseViewModel
import com.depromeet.presentation.util.INTERNET_CONNECTION_ERROR
import com.depromeet.presentation.util.SERVER_CONNECTION_ERROR
import com.depromeet.presentation.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


data class LessonDetailUiState(
    val lessonId: String = "",
    val categoryName: String = "",
    val currentProgressRate: Int = 0,
    val endDate: ArrayList<Int> = ArrayList(),
    val goalProgressRate: Int = 0,
    val isFinished: Boolean = false,
    val lessonName: String = "",
    val message: String = "",
    val presentNumber: Int = 0,
    val price: Int = 0,
    val remainDay: Int = 0,
    val siteName: String = "",
    val startDate: ArrayList<Int> = ArrayList(),
    val totalNumber: Int = 0,
    val wastePrice: Int = 0,
)

@HiltViewModel
class LessonDetailViewModel @Inject constructor(
    private val fetchLessonDetailUseCase: FetchLessonDetailUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private var fetchLessonDetailJob: Job? = null

    val lessonId: String = checkNotNull(savedStateHandle[KEY_LESSON_ID])

    private val _uiState = MutableStateFlow(LessonDetailUiState())
    val uiState: StateFlow<LessonDetailUiState> = _uiState.asStateFlow()

    private val _navigateToUpdateLessonEvent = MutableSharedFlow<LessonDetail>()
    val navigateToUpdateLessonEvent: SharedFlow<LessonDetail> =
        _navigateToUpdateLessonEvent.asSharedFlow()

    private val _navigateToDeleteLessonDialogEvent = MutableSharedFlow<String>()
    val navigateToDeleteLessonDialogEvent: SharedFlow<String> =
        _navigateToDeleteLessonDialogEvent.asSharedFlow()

    fun fetchLessonDetail() {
        if (fetchLessonDetailJob != null) return

        viewModelScope.launch {
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
                            when {
                                result.throwable.message == SERVER_CONNECTION_ERROR -> {
                                    showToast(stringResourcesProvider.getString(R.string.lesson_detail_fetch_fail_by_server_error))
                                }
                                result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                    setInternetError(true)
                                }
                                result.statusCode == UNAUTHORIZED -> {
                                    navigateToExpireDialog()
                                }
                                else -> showToast(stringResourcesProvider.getString(R.string.lesson_detail_fetch_fail))
                            }
                        }
                    }
                    fetchLessonDetailJob = null
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
        _navigateToDeleteLessonDialogEvent.emit(lessonId)
    }

    override fun retry() {
        fetchLessonDetail()
    }

    companion object {
        private const val KEY_LESSON_ID = "lesson_id"
    }
}