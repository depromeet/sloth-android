package com.depromeet.presentation.ui.todaylesson

import androidx.lifecycle.viewModelScope
import com.depromeet.domain.usecase.lesson.FetchTodayLessonListUseCase
import com.depromeet.domain.usecase.lesson.UpdateLessonCountUseCase
import com.depromeet.domain.usecase.login.FetchLoginStatusUseCase
import com.depromeet.domain.util.Result
import com.depromeet.presentation.R
import com.depromeet.presentation.di.StringResourcesProvider
import com.depromeet.presentation.mapper.toUiModel
import com.depromeet.presentation.model.TodayLesson
import com.depromeet.presentation.ui.base.BaseViewModel
import com.depromeet.presentation.util.INTERNET_CONNECTION_ERROR
import com.depromeet.presentation.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


// TODO UiState + stateFlow 를 통한 이벤트 처리
@HiltViewModel
class TodayLessonViewModel @Inject constructor(
    private val fetchLoginStatusUseCase: FetchLoginStatusUseCase,
    private val fetchTodayLessonListUseCase: FetchTodayLessonListUseCase,
    private val updateLessonCountUseCase: UpdateLessonCountUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
) : BaseViewModel() {

    private var fetchTodayLessonJob: Job? = null
    private var updateLessonCountJob: Job? = null

    private var todayLessonList = emptyList<TodayLesson>()

    // private val _uiState = MutableStateFlow<UiState>(UiState())
    // val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _todayLessonUiModelList = MutableStateFlow(emptyList<TodayLessonUiModel>())
    val todayLessonUiModelList: StateFlow<List<TodayLessonUiModel>> = _todayLessonUiModelList.asStateFlow()

    private val _autoLoginEvent = MutableSharedFlow<Boolean>(replay = 1)
    val autoLoginEvent: SharedFlow<Boolean> = _autoLoginEvent.asSharedFlow()

    private val _navigateToLoginEvent = MutableSharedFlow<Unit>()
    val navigateToLoginEvent = _navigateToLoginEvent.asSharedFlow()

    private val _navigateToNotificationListEvent = MutableSharedFlow<Unit>()
    val navigateToNotificationListEvent: SharedFlow<Unit> =
        _navigateToNotificationListEvent.asSharedFlow()

    init {
        checkLoginStatus()
    }

    private val _navigateToRegisterLessonEvent = MutableSharedFlow<Int>()
    val navigateRegisterLessonEvent: SharedFlow<Int> =
        _navigateToRegisterLessonEvent.asSharedFlow()

    private val _navigateToFinishLessonDialogEvent = MutableSharedFlow<String>()
    val navigateToFinishLessonDialogEvent: SharedFlow<String> =
        _navigateToFinishLessonDialogEvent.asSharedFlow()

    private val _navigateToWaitDialogEvent = MutableSharedFlow<Unit>()
    val navigateToWaitDialogEvent: SharedFlow<Unit> = _navigateToWaitDialogEvent.asSharedFlow()

    private fun checkLoginStatus() = viewModelScope.launch {
        _autoLoginEvent.emit(fetchLoginStatusUseCase())
    }

    private fun updateTodayLessonCount(count: Int, lessonId: Int) {
        if (count == 1) {
            increaseTodayLessonCount(lessonId)
        } else {
            decreaseTodayLessonCount(lessonId)
        }
    }

    private fun increaseTodayLessonCount(lessonId: Int) {
        lateinit var newLesson: TodayLesson
        var flag = false

        val lesson = todayLessonList.first { it.lessonId == lessonId }

        val isOutOfRange = lesson.presentNumber > lesson.totalNumber
        if (isOutOfRange) return

        val isUntilTodayFinished = lesson.presentNumber + 1 == lesson.untilTodayNumber
        val isFinished = lesson.presentNumber + 1 == lesson.totalNumber

        when {
            isUntilTodayFinished -> {
                newLesson = lesson.copy(presentNumber = lesson.presentNumber + 1, untilTodayFinished = true)
                flag = true
            }
            isFinished -> {
                newLesson = lesson.copy(presentNumber = lesson.presentNumber + 1)
                flag = true
            }
            else -> {
                newLesson = lesson.copy(presentNumber = lesson.presentNumber + 1)
            }
        }
        val updateLessonList = todayLessonList.map {
            if (it.lessonId == lessonId) {
                newLesson
            } else {
                it
            }
        }
        val isStart = todayLessonList.all { it.presentNumber == 0 } &&
                updateLessonList.any { it.presentNumber > 0 }
        if (isStart) flag = true

        todayLessonList = updateLessonList
        if (flag) setTodayLessonList()
    }

    private fun decreaseTodayLessonCount(lessonId: Int) {
        lateinit var newLesson: TodayLesson
        var flag = false

        val lesson = todayLessonList.first { it.lessonId == lessonId }

        val isOutOfRange = lesson.presentNumber <= 0
        if (isOutOfRange) return

        val isUntilTodayNotFinished = lesson.presentNumber == lesson.untilTodayNumber
        val isNotFinished = lesson.presentNumber == lesson.totalNumber

        when {
            isUntilTodayNotFinished -> {
                newLesson = lesson.copy(presentNumber = lesson.presentNumber - 1, untilTodayFinished = false)
                flag = true
            }
            isNotFinished -> {
                newLesson = lesson.copy(presentNumber = lesson.presentNumber - 1)
                flag = true
            }
            else -> {
                newLesson = lesson.copy(presentNumber = lesson.presentNumber - 1)
            }
        }

        val updateLessonList = todayLessonList.map {
            if (it.lessonId == lessonId) {
                newLesson
            } else {
                it
            }
        }
        val isNotStarted = todayLessonList.any { it.presentNumber > 0 } &&
                updateLessonList.all { it.presentNumber == 0 }
        if (isNotStarted) flag = true

        todayLessonList = updateLessonList
        if (flag) setTodayLessonList()
    }

    fun fetchTodayLessonList() {
        if (fetchTodayLessonJob != null) return

        fetchTodayLessonJob = viewModelScope.launch {
            fetchTodayLessonListUseCase().onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        setInternetError(false)
                        todayLessonList = result.data.toUiModel()
                        setTodayLessonList()
                    }
                    is Result.Error -> {
                        when {
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
                fetchTodayLessonJob = null
            }
        }
    }

    private fun setTodayLessonList() {
        _todayLessonUiModelList.update {
            if (todayLessonList.isEmpty()) {
                listOf(
                    TodayLessonUiModel.TodayLessonHeaderItem(TodayLessonType.EMPTY),
                    TodayLessonUiModel.TodayLessonTitleItem(TodayLessonType.EMPTY),
                    TodayLessonUiModel.TodayLessonEmptyItem
                )
            } else {
                todayLessonList.groupBy {
                    it.untilTodayFinished
                }.values.map {
                    it.map { lesson ->
                        if (lesson.untilTodayFinished) {
                            TodayLessonUiModel.TodayLessonFinishedItem(lesson)
                        } else {
                            TodayLessonUiModel.TodayLessonDoingItem(lesson)
                        }
                    }
                }.flatMap {
                    when (it.first()) {
                        is TodayLessonUiModel.TodayLessonDoingItem -> {
                            it.toMutableList().apply {
                                add(0, TodayLessonUiModel.TodayLessonTitleItem(TodayLessonType.DOING))
                            }
                        }
                        is TodayLessonUiModel.TodayLessonFinishedItem -> {
                            it.toMutableList().apply {
                                add(0, TodayLessonUiModel.TodayLessonTitleItem(TodayLessonType.FINISHED))
                            }
                        }
                        else -> return
                    }
                }.let {
                    val isFinished = it.find { it is TodayLessonUiModel.TodayLessonDoingItem } == null
                    val isNotStarted = it.find { it is TodayLessonUiModel.TodayLessonFinishedItem } == null &&
                            it.filterIsInstance<TodayLessonUiModel.TodayLessonDoingItem>().all { it.todayLesson.presentNumber == 0 }
                    when {
                        isFinished -> {
                            it.toMutableList().apply {
                                add(0, TodayLessonUiModel.TodayLessonHeaderItem(TodayLessonType.FINISHED))
                            }
                        }
                        isNotStarted -> {
                            it.toMutableList().apply {
                                add(0, TodayLessonUiModel.TodayLessonHeaderItem(TodayLessonType.NOT_START))
                            }
                        }
                        else -> {
                            it.toMutableList().apply {
                                add(0, TodayLessonUiModel.TodayLessonHeaderItem(TodayLessonType.DOING))
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateLessonCount(count: Int, lesson: TodayLesson) {
        updateTodayLessonCount(count, lesson.lessonId)
        if (updateLessonCountJob != null) return

        updateLessonCountJob = viewModelScope.launch {
            updateLessonCountUseCase(count = count, lessonId = lesson.lessonId)
                .onEach { result ->
                    setLoading(result is Result.Loading)
                }.collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> Unit
                        is Result.Error -> {
                            when {
                                result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                    showToast(stringResourcesProvider.getString(R.string.lesson_update_count_fail_by_internet_error))
                                }
                                result.statusCode == UNAUTHORIZED -> {
                                    navigateToExpireDialog()
                                }
                                else -> showToast(stringResourcesProvider.getString(R.string.lesson_update_count_fail))
                            }
                        }
                    }
                    updateLessonCountJob = null
                }
        }
    }

    fun navigateToRegisterLesson(fragmentId: Int) = viewModelScope.launch {
        _navigateToRegisterLessonEvent.emit(fragmentId)
    }

    fun navigateToFinishLessonDialog(lessonId: String) = viewModelScope.launch {
        _navigateToFinishLessonDialogEvent.emit(lessonId)
    }

    fun onNotificationClicked() = viewModelScope.launch {
        _navigateToNotificationListEvent.emit(Unit)
    }

    override fun retry() {
        fetchTodayLessonList()
    }
}

//data class UiState (
//    val todayLessonList: List<TodayLessonResponse> = emptyList(),
//    val isLoading: Boolean = false,
//    val errorMessage: String? = null,
//)
