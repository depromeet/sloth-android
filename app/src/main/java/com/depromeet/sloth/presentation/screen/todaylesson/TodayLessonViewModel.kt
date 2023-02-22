package com.depromeet.sloth.presentation.screen.todaylesson

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.usecase.lesson.FetchTodayLessonListUseCase
import com.depromeet.sloth.domain.usecase.lesson.UpdateLessonCountUseCase
import com.depromeet.sloth.domain.usecase.login.FetchLoginStatusUseCase
import com.depromeet.sloth.domain.usecase.member.FetchTodayLessonOnBoardingStatusUseCase
import com.depromeet.sloth.extensions.cancelIfActive
import com.depromeet.sloth.presentation.screen.base.BaseViewModel
import com.depromeet.sloth.util.INTERNET_CONNECTION_ERROR
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


// TODO OnBoarding Validation 은 login 처럼 viewModel init 내에서 딱 한번만 호출하여 매번 호출되지 않도록
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

    private val _checkTodayLessonOnBoardingCompleteEvent = MutableSharedFlow<Boolean>(1)
    val checkTodayLessonOnBoardingCompleteEvent: SharedFlow<Boolean> =
        _checkTodayLessonOnBoardingCompleteEvent.asSharedFlow()

    private val _todayLessonOnBoardingComplete = MutableStateFlow(false)
    val todayLessonOnBoardingComplete: StateFlow<Boolean> = _todayLessonOnBoardingComplete.asStateFlow()

    private val _todayLessonList = MutableStateFlow(emptyList<TodayLessonResponse>())
    val todayLessonList: StateFlow<List<TodayLessonResponse>> = _todayLessonList.asStateFlow()

    private val _todayLessonUiModelList = MutableStateFlow(emptyList<TodayLessonUiModel>())
    val todayLessonUiModelList: StateFlow<List<TodayLessonUiModel>> = _todayLessonUiModelList.asStateFlow()

    init {
        checkLoginStatus()
    }

    private val _navigateToRegisterLessonEvent = MutableSharedFlow<Unit>()
    val navigateRegisterLessonEvent: SharedFlow<Unit> =
        _navigateToRegisterLessonEvent.asSharedFlow()

    private val _navigateToOnBoardingRegisterLessonDialogEvent = MutableSharedFlow<Unit>()
    val navigateToOnBoardingRegisterLessonDialogEvent: SharedFlow<Unit> =
        _navigateToOnBoardingRegisterLessonDialogEvent.asSharedFlow()

    private val _navigateToFinishLessonDialogEvent = MutableSharedFlow<String>()
    val navigateToFinishLessonDialogEvent: SharedFlow<String> =
        _navigateToFinishLessonDialogEvent.asSharedFlow()

    private val _navigateToWaitDialogEvent = MutableSharedFlow<Unit>()
    val navigateToWaitDialogEvent: SharedFlow<Unit> = _navigateToWaitDialogEvent.asSharedFlow()

    private fun checkLoginStatus() = viewModelScope.launch {
        _autoLoginEvent.emit(fetchLoginStatusUseCase())
    }

    fun checkTodayLessonOnBoardingComplete() = viewModelScope.launch {
        _checkTodayLessonOnBoardingCompleteEvent.emit(fetchTodayLessonOnBoardingStatusUseCase())
    }

    // TODO update function 을 이용 해야 할듯
    // 사실 race condition 일어날 조건은 아니긴 함
    private fun updateTodayLessonCount(count: Int, lessonId: Int) {
        if (count == 1) {
            increaseTodayLessonCount(lessonId)
        } else {
            decreaseTodayLessonCount(lessonId)
        }
    }

    //TODO 갱신이 안되는 이슈
    private fun increaseTodayLessonCount(lessonId: Int) {
        Timber.d("${_todayLessonList.value}")
        lateinit var newLesson: TodayLessonResponse
        var flag = false

        val currentLessonList = _todayLessonList.value.toMutableList()
        // lessonId 를 통해 갱신할 강의 찾기
        val lesson = currentLessonList.first { it.lessonId == lessonId }
        val isOutOfRange = lesson.presentNumber <= lesson.totalNumber
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
        // currentLessonList 내에 lessonId 를 통해 강의를 찾아 교체
        currentLessonList.map {
            if (it.lessonId == lessonId) {
                newLesson
            } else {
                lesson
            }
        }

        val isStart = _todayLessonList.value.all { it.presentNumber == 0 } &&
                currentLessonList.any { it.presentNumber > 0 }
        if (isStart) flag = true

        // _todayLessonList.value = currentLessonList
        _todayLessonList.update { currentLessonList }
        if (flag) {
            setTodayLessonList()
        }
        Timber.d("${_todayLessonList.value}")
    }

    // TODO 갱신이 안되는 이슈
    private fun decreaseTodayLessonCount(lessonId: Int) {
        Timber.d("${_todayLessonList.value}")
        lateinit var newLesson: TodayLessonResponse
        var flag = false

        val currentLessonList = _todayLessonList.value.toMutableList()
        // lessonId 를 통해 갱신할 강의 찾기
        val lesson = currentLessonList.first { it.lessonId == lessonId }
        val isOutOfRange = lesson.presentNumber <= 0
        if (isOutOfRange) return

        val isUntilTodayNotFinished = lesson.presentNumber <= lesson.untilTodayNumber
        val isNotFinished = lesson.presentNumber <= lesson.totalNumber

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
        // currentLessonList 내에 lessonId 를 통해 강의를 찾아 교체
        currentLessonList.map {
            if (it.lessonId == lessonId) {
                newLesson
            } else {
                lesson
            }
        }
        val isNotStarted = currentLessonList.all { it.presentNumber == 0 }
        if (isNotStarted) flag = true

        // _todayLessonList.value = currentLessonList
        _todayLessonList.update { currentLessonList }
        if (flag) {
            setTodayLessonList()
        }
        Timber.d("${_todayLessonList.value}")
    }

    fun fetchTodayLessonList() {
        todayLessonJob.cancelIfActive()
        todayLessonJob = viewModelScope.launch {
            fetchTodayLessonListUseCase().onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        setInternetError(false)
                        _todayLessonList.value = result.data
                        setTodayLessonList()
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
                }.values.map {
                    it.map { lesson ->
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

    // TODO 리스트 갱신이 안 이루어지는 이슈
    fun updateLessonCount(count: Int, lesson: TodayLessonResponse) {
        updateLessonCountJob.cancelIfActive()
        updateLessonCountJob = viewModelScope.launch {
            updateLessonCountUseCase(count = count, lessonId = lesson.lessonId)
                .onEach { result ->
                    setLoading(result is Result.Loading)
                }.collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> {
                            updateTodayLessonCount(count, result.data.lessonId)
                        }
                        is Result.Error -> {
                            if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                                showToast(stringResourcesProvider.getString(R.string.lesson_update_count_fail_by_internet_error))
                            } else if (result.statusCode == UNAUTHORIZED) {
                                navigateToExpireDialog()
                            } else {
                                showToast(stringResourcesProvider.getString(R.string.lesson_update_count_fail))
                            }
                        }
                    }
                }
        }
    }

    fun navigateToRegisterLesson() = viewModelScope.launch {
        _navigateToRegisterLessonEvent.emit(Unit)
    }

    fun navigateToWaitDialog() = viewModelScope.launch {
        _navigateToWaitDialogEvent.emit(Unit)
    }

    fun navigateToFinishLessonDialog(lessonId: String) = viewModelScope.launch {
        _navigateToFinishLessonDialogEvent.emit(lessonId)
    }

    override fun retry() {
        fetchTodayLessonList()
    }
}
