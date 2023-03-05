package com.depromeet.sloth.presentation.ui.todaylesson

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.usecase.lesson.FetchTodayLessonListUseCase
import com.depromeet.sloth.domain.usecase.lesson.UpdateLessonCountUseCase
import com.depromeet.sloth.domain.usecase.login.FetchLoginStatusUseCase
import com.depromeet.sloth.domain.usecase.member.FetchTodayLessonOnBoardingStatusUseCase
import com.depromeet.sloth.presentation.ui.base.BaseViewModel
import com.depromeet.sloth.util.INTERNET_CONNECTION_ERROR
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


// TODO UiState + stateFlow 를 통한 이벤트 처리
@HiltViewModel
class TodayLessonViewModel @Inject constructor(
    private val fetchLoginStatusUseCase: FetchLoginStatusUseCase,
    private val fetchTodayLessonOnBoardingStatusUseCase: FetchTodayLessonOnBoardingStatusUseCase,
    private val fetchTodayLessonListUseCase: FetchTodayLessonListUseCase,
    private val updateLessonCountUseCase: UpdateLessonCountUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
) : BaseViewModel() {

    private var todayLessonJob: Job? = null
    private var updateLessonCountJob: Job? = null

    private val _autoLoginEvent = MutableSharedFlow<Boolean>(replay = 1)
    val autoLoginEvent: SharedFlow<Boolean> = _autoLoginEvent.asSharedFlow()

    private val _checkTodayLessonOnBoardingCompleteEvent = MutableSharedFlow<Boolean>(replay = 1)
    val checkTodayLessonOnBoardingCompleteEvent: SharedFlow<Boolean> =
        _checkTodayLessonOnBoardingCompleteEvent.asSharedFlow()

    private val _navigateToLoginEvent = MutableSharedFlow<Unit>()
    val navigateToLoginEvent = _navigateToLoginEvent.asSharedFlow()

    private val _navigateToTodayLessonOnBoardingEvent = MutableSharedFlow<Unit>()
    val navigateToTodayLessonOnBoardingEvent = _navigateToTodayLessonOnBoardingEvent.asSharedFlow()


    private val _todayLessonList = MutableStateFlow(emptyList<TodayLessonResponse>())
    val todayLessonList: StateFlow<List<TodayLessonResponse>> = _todayLessonList.asStateFlow()

    private val _todayLessonUiModelList = MutableStateFlow(emptyList<TodayLessonUiModel>())
    val todayLessonUiModelList: StateFlow<List<TodayLessonUiModel>> = _todayLessonUiModelList.asStateFlow()

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
//        val isLoggedIn = fetchLoginStatusUseCase()
//        if (isLoggedIn) checkTodayLessonOnBoardingComplete()
//        else _navigateToLoginEvent.emit(Unit)
    }

    fun checkTodayLessonOnBoardingComplete() = viewModelScope.launch {
        _checkTodayLessonOnBoardingCompleteEvent.emit(fetchTodayLessonOnBoardingStatusUseCase())
//        val isTodayLessonOnBoardingComplete = fetchTodayLessonOnBoardingStatusUseCase()
//        if (!isTodayLessonOnBoardingComplete) _navigateToTodayLessonOnBoardingEvent.emit(Unit)
//        else fetchTodayLessonList()
    }

    private fun updateTodayLessonCount(count: Int, lessonId: Int) {
        if (count == 1) {
            increaseTodayLessonCount(lessonId)
        } else {
            decreaseTodayLessonCount(lessonId)
        }
    }

    private fun rollbackTodayLessonCount(count: Int, lessonId: Int) {
        if (count == 1) {
            decreaseTodayLessonCount(lessonId)
        } else {
            increaseTodayLessonCount(lessonId)
        }
    }

    private fun increaseTodayLessonCount(lessonId: Int) {
        lateinit var newLesson: TodayLessonResponse
        var flag = false

        val currentLessonList = _todayLessonList.value.toMutableList()
        val lesson = currentLessonList.first { it.lessonId == lessonId }

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
        val updateLessonList = currentLessonList.map {
            if (it.lessonId == lessonId) {
                newLesson
            } else {
                it
            }
        }
        val isStart = currentLessonList.all { it.presentNumber == 0 } &&
                updateLessonList.any { it.presentNumber > 0 }
        if (isStart) flag = true

        _todayLessonList.update { updateLessonList }
        if (flag) setTodayLessonList()
    }

    private fun decreaseTodayLessonCount(lessonId: Int) {
        lateinit var newLesson: TodayLessonResponse
        var flag = false

        val currentLessonList = _todayLessonList.value.toMutableList()
        val lesson = currentLessonList.first { it.lessonId == lessonId }

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

        val updateLessonList = currentLessonList.map {
            if (it.lessonId == lessonId) {
                newLesson
            } else {
                it
            }
        }
        val isNotStarted = currentLessonList.any { it.presentNumber > 0 } &&
                updateLessonList.all { it.presentNumber == 0 }
        if (isNotStarted) flag = true

        _todayLessonList.update { updateLessonList }
        if (flag) setTodayLessonList()
    }

    fun fetchTodayLessonList() {
        if (todayLessonJob != null) return

        todayLessonJob = viewModelScope.launch {
            fetchTodayLessonListUseCase().onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        setInternetError(false)
                        _todayLessonList.update { result.data }
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
                todayLessonJob = null
            }
        }
    }

    private fun setTodayLessonList() {
        _todayLessonUiModelList.update {
            if (_todayLessonList.value.isEmpty()) {
                listOf(
                    TodayLessonUiModel.TodayLessonHeaderItem(TodayLessonType.EMPTY),
                    TodayLessonUiModel.TodayLessonTitleItem(TodayLessonType.EMPTY),
                    TodayLessonUiModel.TodayLessonEmptyItem
                )
            } else {
                _todayLessonList.value.groupBy {
                    it.untilTodayFinished
                }.values.map { todayLessonList ->
                    todayLessonList.map { lesson ->
                        if (lesson.untilTodayFinished) {
                            TodayLessonUiModel.TodayLessonFinishedItem(lesson)
                        } else {
                            TodayLessonUiModel.TodayLessonDoingItem(lesson)
                        }
                    }
                }.flatMap { todayLessons ->
                    when (todayLessons.first()) {
                        is TodayLessonUiModel.TodayLessonDoingItem -> {
                            todayLessons.toMutableList().apply {
                                add(0, TodayLessonUiModel.TodayLessonTitleItem(TodayLessonType.DOING))
                            }
                        }
                        is TodayLessonUiModel.TodayLessonFinishedItem -> {
                            todayLessons.toMutableList().apply {
                                add(0, TodayLessonUiModel.TodayLessonTitleItem(TodayLessonType.FINISHED))
                            }
                        }
                        else -> return
                    }
                }.let { lessons ->
                    val isFinished = lessons.find { it is TodayLessonUiModel.TodayLessonDoingItem } == null
                    val isNotStarted = lessons.find { it is TodayLessonUiModel.TodayLessonFinishedItem } == null &&
                            lessons.filterIsInstance<TodayLessonUiModel.TodayLessonDoingItem>().all { it.todayLesson.presentNumber == 0 }
                    when {
                        isFinished -> {
                            lessons.toMutableList().apply {
                                add(0, TodayLessonUiModel.TodayLessonHeaderItem(TodayLessonType.FINISHED))
                            }
                        }
                        isNotStarted -> {
                            lessons.toMutableList().apply {
                                add(0, TodayLessonUiModel.TodayLessonHeaderItem(TodayLessonType.NOT_START))
                            }
                        }
                        else -> {
                            lessons.toMutableList().apply {
                                add(0, TodayLessonUiModel.TodayLessonHeaderItem(TodayLessonType.DOING))
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateLessonCount(count: Int, lesson: TodayLessonResponse) {
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
                            // adapter 의 애니메이션 기능을 내부에서 수행시키지 못함
                            // rollbackTodayLessonCount(count, lesson.lessonId)
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
