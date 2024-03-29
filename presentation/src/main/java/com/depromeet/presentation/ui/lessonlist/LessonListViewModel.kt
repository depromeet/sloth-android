package com.depromeet.presentation.ui.lessonlist

import androidx.lifecycle.viewModelScope
import com.depromeet.domain.usecase.lesson.FetchLessonListUseCase
import com.depromeet.domain.usecase.userprofile.CheckLessonListOnBoardingStatusUseCase
import com.depromeet.domain.usecase.userprofile.UpdateLessonListOnBoardingStatusUseCase
import com.depromeet.domain.util.Result
import com.depromeet.presentation.R
import com.depromeet.presentation.di.StringResourcesProvider
import com.depromeet.presentation.mapper.toUiModel
import com.depromeet.presentation.model.Lesson
import com.depromeet.presentation.ui.base.BaseViewModel
import com.depromeet.presentation.util.INTERNET_CONNECTION_ERROR
import com.depromeet.presentation.util.SERVER_CONNECTION_ERROR
import com.depromeet.presentation.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


// TODO 변수 명에 ~list 지양 by Clean Code
@HiltViewModel
class LessonListViewModel @Inject constructor(
    private val fetchLessonListUseCase: FetchLessonListUseCase,
    private val checkLessonListOnBoardingStatusUseCase: CheckLessonListOnBoardingStatusUseCase,
    private val updateLessonListOnBoardingStatusUseCase: UpdateLessonListOnBoardingStatusUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
) : BaseViewModel() {

    private var fetchLessonListJob: Job? = null

    // private val _uiState = MutableStateFlow<UiState>(UiState())
    // val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _checkLessonListOnBoardingCompleteEvent = MutableSharedFlow<Boolean>(1)
    val checkLessonListOnBoardingCompleteEvent: SharedFlow<Boolean> =
        _checkLessonListOnBoardingCompleteEvent.asSharedFlow()

    private val _navigateToOnBoardingCheckDetailEvent = MutableSharedFlow<Unit>()
    val navigateToOnBoardingCheckDetailEvent: SharedFlow<Unit> =
        _navigateToOnBoardingCheckDetailEvent.asSharedFlow()

    private val _navigateToRegisterLessonEvent = MutableSharedFlow<Int>()
    val navigateRegisterLessonEvent: SharedFlow<Int> =
        _navigateToRegisterLessonEvent.asSharedFlow()

    private val _navigateToNotificationListEvent = MutableSharedFlow<Unit>()
    val navigateToNotificationListEvent: SharedFlow<Unit> =
        _navigateToNotificationListEvent.asSharedFlow()

    private val _navigateToLessonDetailEvent = MutableSharedFlow<String>()
    val navigateToLessonDetailEvent: SharedFlow<String> =
        _navigateToLessonDetailEvent.asSharedFlow()

    private val _lessonList = MutableStateFlow<List<LessonListUiModel>>(emptyList())
    val lessonList: StateFlow<List<LessonListUiModel>> = _lessonList.asStateFlow()

    fun fetchLessonList() {
        if (fetchLessonListJob != null) return

        fetchLessonListJob = viewModelScope.launch {
            fetchLessonListUseCase()
                .onEach { result ->
                    setLoading(result is Result.Loading)
                }.collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> {
                            setInternetError(false)
                            setLessonList(result.data.toUiModel())
                            checkLessonListOnBoardingComplete()
                        }
                        is Result.Error -> {
                            when {
                                result.throwable.message == SERVER_CONNECTION_ERROR -> {
                                    showToast(stringResourcesProvider.getString(R.string.lesson_fetch_fail_by_server_error))
                                }
                                result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                    setInternetError(true)
                                }
                                result.statusCode == UNAUTHORIZED -> {
                                    navigateToExpireDialog()
                                }
                                else -> showToast(stringResourcesProvider.getString(R.string.lesson_fetch_fail))
                            }
                        }
                    }
                    fetchLessonListJob = null
                }
        }
    }

//    fun fetchLessonList() {
//        if (lessonListJob != null) return
//
//        lessonListJob = viewModelScope.launch {
//            try {
//                fetchLessonListUseCase()
//                    .onEach { result ->
//                        setLoading(result is Result.Loading)
//                    }.collect { result ->
//                        when (result) {
//                            is Result.Loading -> return@collect
//                            is Result.Success -> {
//                                setInternetError(false)
//                                setLessonList(result.data.toUiModel())
//                            }
//                            is Result.Error -> {
//                                if (result.statusCode == UNAUTHORIZED) {
//                                    navigateToExpireDialog()
//                                }
//                            }
//                        }
//                    }
//            } catch (ioe: IOException) {
//                showToast(stringResourcesProvider.getString(R.string.lesson_fetch_fail))
//                setInternetError(true)
//            }
//            finally { lessonListJob = null }
//        }
//    }

    private fun setLessonList(result: List<Lesson>) {
        _lessonList.update {
            if (result.isEmpty()) {
                listOf(LessonListUiModel.LessonListEmptyItem)
            } else {
                result.groupBy {
                    it.lessonStatus
                }.values.map { lessonList ->
                    lessonList.map { lesson ->
                        when {
                            (lesson.isFinished || lesson.lessonStatus == "PAST") -> LessonListUiModel.LessonListPastItem(lesson)
                            (lesson.lessonStatus == "PLAN") -> LessonListUiModel.LessonListPlanItem(lesson)
                            else -> LessonListUiModel.LessonListCurrentItem(lesson)
                        }
                    }
                }.map { lessonUiModelList ->
                    lessonUiModelList.toMutableList().apply {
                        add(
                            0,
                            when (lessonUiModelList.first()) {
                                is LessonListUiModel.LessonListCurrentItem ->
                                    LessonListUiModel.LessonListTitleItem(LessonListType.CURRENT, this.size)
                                is LessonListUiModel.LessonListPlanItem ->
                                    LessonListUiModel.LessonListTitleItem(LessonListType.PLAN, this.size)
                                is LessonListUiModel.LessonListPastItem ->
                                    LessonListUiModel.LessonListTitleItem(LessonListType.PAST, this.size)
                                else -> return@apply
                            }
                        )
                    }
                }.flatten()
            }
        }
    }

    fun navigateToOnBoardingCheckDetail() = viewModelScope.launch {
        updateOnBoardingLessonListStatus(true)
        _navigateToOnBoardingCheckDetailEvent.emit(Unit)
    }

    private fun checkLessonListOnBoardingComplete() = viewModelScope.launch {
        _checkLessonListOnBoardingCompleteEvent.emit(checkLessonListOnBoardingStatusUseCase())
    }

    fun navigateToRegisterLesson(fragmentId: Int) = viewModelScope.launch {
        _navigateToRegisterLessonEvent.emit(fragmentId)
    }

    fun onNotificationClicked() = viewModelScope.launch {
        _navigateToNotificationListEvent.emit(Unit)
    }

    fun navigateToLessonDetail(lesson: Lesson) {
        if (lesson.lessonStatus == "PAST") return

        viewModelScope.launch {
            _navigateToLessonDetailEvent.emit(lesson.lessonId.toString())
        }
    }

    private fun updateOnBoardingLessonListStatus(flag: Boolean) = viewModelScope.launch {
        updateLessonListOnBoardingStatusUseCase(flag)
    }

    override fun retry() {
        fetchLessonList()
    }
}

//data class UiState (
//    val lessonList: List<LessonListResponse> = emptyList(),
//    val isLoading: Boolean = false,
//    val errorMessage: String? = null,
//)
