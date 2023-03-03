package com.depromeet.sloth.presentation.screen.todaylesson

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse
import com.depromeet.sloth.data.model.response.lesson.UpdateLessonCountResponse
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.usecase.lesson.FetchTodayLessonListUseCase
import com.depromeet.sloth.domain.usecase.lesson.UpdateLessonCountUseCase
import com.depromeet.sloth.domain.usecase.login.FetchLoginStatusUseCase
import com.depromeet.sloth.domain.usecase.member.FetchTodayLessonOnBoardingStatusUseCase
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

    private val _todayLessonList = MutableStateFlow(emptyList<TodayLessonResponse>())
    val todayLessonList: StateFlow<List<TodayLessonResponse>> = _todayLessonList.asStateFlow()

    private val _todayLessonUiModelList = MutableStateFlow(emptyList<TodayLessonUiModel>())
    val todayLessonUiModelList: StateFlow<List<TodayLessonUiModel>> = _todayLessonUiModelList.asStateFlow()

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

    fun checkTodayLessonOnBoardingComplete() = viewModelScope.launch {
        _checkTodayLessonOnBoardingCompleteEvent.emit(fetchTodayLessonOnBoardingStatusUseCase())
    }

    private fun updateTodayLessonCount(count: Int, updateLesson: UpdateLessonCountResponse) {
        if (count == 1) {
            increaseTodayLessonCount(updateLesson.lessonId, updateLesson.presentNumber)
        } else {
            decreaseTodayLessonCount(updateLesson.lessonId, updateLesson.presentNumber)
        }
    }

    // TODO isStarted 조건 만족시 화면 갱신이 안되는 이슈
    // flag 가 변경 되지 않음
    // 리스트 수정 작업 이전에 이미 갱신이 이뤄짐
    // 어댑터에서 ++ 해준게 뷰모델 데이터에 반영이 되는건가?
    // 온보딩에서 코드와 투데이화면에서의 코드가 다른 이유
    private fun increaseTodayLessonCount(lessonId: Int, updateCount: Int) {
        lateinit var newLesson: TodayLessonResponse
        var flag = false

        val currentLessonList = _todayLessonList.value.toMutableList()
        // lessonId 를 통해 갱신할 강의 찾기
        val lesson = currentLessonList.first { it.lessonId == lessonId }
        Timber.d("$lesson")
        val isOutOfRange = lesson.presentNumber > lesson.totalNumber
        if (isOutOfRange) return

        val isUntilTodayFinished = lesson.presentNumber == lesson.untilTodayNumber
        val isFinished = lesson.presentNumber == lesson.totalNumber

        // Timber.d("$currentLessonList")
        when {
            isUntilTodayFinished -> {
                newLesson = lesson.copy(presentNumber = updateCount, untilTodayFinished = true)
                flag = true
            }
            isFinished -> {
                newLesson = lesson.copy(presentNumber = updateCount)
                flag = true
            }
            else -> {
                newLesson = lesson.copy(presentNumber = updateCount)
            }
        }
        Timber.d("$newLesson")
        // currentLessonList 내에 lessonId 를 통해 강의를 찾아 교체
        val updateLessonList = currentLessonList.map {
            if (it.lessonId == lessonId) {
                newLesson
            } else {
                it
            }
        }

        // Timber.d("$updateLessonList")
        val isStart = currentLessonList.all { it.presentNumber == 0 } &&
                updateLessonList.any { it.presentNumber > 0 }
        if (isStart) flag = true

        // _todayLessonList.value = updateLessonList
        _todayLessonList.update { updateLessonList }
        if (flag) setTodayLessonList()
    }

    // TODO isNotStarted 조건 만족시 화면 갱신이 안되는 이슈
    // flag 가 변경 되지 않음
    // 리스트 수정 작업 이전에 이미 갱신이 이뤄짐
    // 어댑터에서 -- 해준게 뷰모델 데이터에 반영이 되는건가? 그렇다
    // 온보딩에서 코드와 투데이화면에서의 코드가 다른 이유
    private fun decreaseTodayLessonCount(lessonId: Int, updateCount: Int) {
        lateinit var newLesson: TodayLessonResponse
        var flag = false

        val currentLessonList = _todayLessonList.value.toMutableList()
        // lessonId 를 통해 갱신할 강의 찾기
        val lesson = currentLessonList.first { it.lessonId == lessonId }

        Timber.d("$lesson")
        val isOutOfRange = lesson.presentNumber <= 0
        if (isOutOfRange) return

        // 선반영 되고 있다는 것을 알고있음
        // Timber.d("$currentLessonList")
        val isUntilTodayNotFinished = lesson.presentNumber + 1 == lesson.untilTodayNumber
        val isNotFinished = lesson.presentNumber + 1 == lesson.totalNumber

        when {
            isUntilTodayNotFinished -> {
                newLesson = lesson.copy(presentNumber = updateCount, untilTodayFinished = false)
                flag = true
            }
            isNotFinished -> {
                newLesson = lesson.copy(presentNumber = updateCount)
                flag = true
            }
            else -> {
                newLesson = lesson.copy(presentNumber = updateCount)
            }
        }
        Timber.d("$newLesson")

        // currentLessonList 내에 lessonId 를 통해 강의를 찾아 교체
        val updateLessonList = currentLessonList.map {
            if (it.lessonId == lessonId) {
                newLesson
            } else {
                it
            }
        }
        Timber.d("$updateLessonList")
        val isNotStarted = currentLessonList.any { it.presentNumber > 0 } &&
                updateLessonList.all { it.presentNumber == 0 }
        if (isNotStarted) flag = true

        // _todayLessonList.value = updateLessonList
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
                        // _todayLessonList.value = result.data
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
                            else -> {
                                showToast(stringResourcesProvider.getString(R.string.lesson_fetch_fail))
                            }
                        }
                    }
                }
                todayLessonJob = null
            }
        }
    }

    private fun setTodayLessonList() {
        Timber.d("setTodayLessonList() 호출")
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
        if (updateLessonCountJob != null) return

        updateLessonCountJob = viewModelScope.launch {
            updateLessonCountUseCase(count = count, lessonId = lesson.lessonId)
                .onEach { result ->
                    setLoading(result is Result.Loading)
                }.collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> {
                            //TODO data 로 받아온 presentNumber 를 local update 에 쓰면 동기화 오류 문제가 없지 않을까?
                            updateTodayLessonCount(count, result.data)
                        }
                        is Result.Error -> {
                            when {
                                result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                    showToast(stringResourcesProvider.getString(R.string.lesson_update_count_fail_by_internet_error))
                                }
                                result.statusCode == UNAUTHORIZED -> {
                                    navigateToExpireDialog()
                                }
                                else -> {
                                    showToast(stringResourcesProvider.getString(R.string.lesson_update_count_fail))
                                }
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
