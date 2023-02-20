package com.depromeet.sloth.presentation.screen.todaylesson

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.usecase.lesson.FetchTodayLessonListUseCase
import com.depromeet.sloth.domain.usecase.lesson.UpdateLessonCountUseCase
import com.depromeet.sloth.domain.usecase.login.FetchLoginStatusUseCase
import com.depromeet.sloth.domain.usecase.member.FetchTodayLessonOnBoardingStatusUseCase
import com.depromeet.sloth.domain.usecase.member.UpdateTodayLessonOnBoardingStatusUseCase
import com.depromeet.sloth.extensions.cancelIfActive
import com.depromeet.sloth.presentation.screen.base.BaseViewModel
import com.depromeet.sloth.util.INTERNET_CONNECTION_ERROR
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


// TODO OnBoarding Validation 은 login 처럼 viewModel init 내에서 딱 한번만 호출하여 매번 호출되지 않도록
@HiltViewModel
class TodayLessonViewModel @Inject constructor(
    private val fetchLoginStatusUseCase: FetchLoginStatusUseCase,
    private val fetchTodayLessonOnBoardingStatusUseCase: FetchTodayLessonOnBoardingStatusUseCase,
    private val updateTodayLessonOnBoardingStatusUseCase: UpdateTodayLessonOnBoardingStatusUseCase,
    private val fetchLessonTodayListUseCase: FetchTodayLessonListUseCase,
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

    private val _todayLessonOnBoardingComplete = MutableStateFlow<Boolean>(false)
    val todayLessonOnBoardingComplete: StateFlow<Boolean> = _todayLessonOnBoardingComplete.asStateFlow()

    private val _todayLessonList = MutableStateFlow(emptyList<TodayLessonUiModel>())
    val todayLessonList: StateFlow<List<TodayLessonUiModel>> = _todayLessonList.asStateFlow()

    private val _onBoardingList = MutableStateFlow(emptyList<OnBoardingUiModel>())
    val onBoardingList: StateFlow<List<OnBoardingUiModel>> = _onBoardingList.asStateFlow()

    init {
        //TODO OnBoarding check 도..
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

    fun updateTodayLessonOnBoardingStatus() {
        _todayLessonOnBoardingComplete.value = true
    }

    fun setOnBoardingItem() {
        _onBoardingList.update { list ->
            list.toMutableList().apply {
                add(
                    OnBoardingUiModel.OnBoardingDoingItem(
                        TodayLessonResponse(
                            "튜토리얼",
                            0,
                            "나공이와 대결하기\n: 게이지를 빨리 채워 완료해봐요!",
                            0,
                            1,
                            "",
                            false,
                            3,
                            3
                        )
                    )
                )
            }
        }
    }

    // TODO update function 을 이용 해야 할듯
    // 사실 race condition 일어날 조건은 아니긴 함
//    fun updateOnBoardingItemCount(count: Int, lessonId: String) {
//        if (count == 1) {
//            increaseOnBoardingItemCount()
//        } else {
//            decreaseOnBoardingItemCount()
//        }
//        refreshOnBoardingItem()
//    }

//    private fun increaseOnBoardingItemCount() {
//        _onBoardingList.value = onBoardingList.value.mapIndexed { index, item ->
//            if (index == 0) {
//                val isOutOfRange = item.presentNumber == item.totalNumber
//                if (isOutOfRange) return
//                item.copy(presentNumber = item.presentNumber.plus(1))
//            } else item
//        }
//    }

//    private fun decreaseOnBoardingItemCount() {
//        _onBoardingList.value = onBoardingList.value.mapIndexed { index, item ->
//            if (index == 0) {
//                val isOutOfRange = item.presentNumber == 0
//                if (isOutOfRange) return
//                item.copy(presentNumber = item.presentNumber.minus(1))
//            } else item
//        }
//    }

//    private fun refreshOnBoardingItem() {
//        var isNotFinished = true
//        _onBoardingList.value = onBoardingList.value.mapIndexed { index, item ->
//            if (index == 0) {
//                isNotFinished = item.presentNumber < item.totalNumber
//                if (isNotFinished) return
//                item.copy(untilTodayFinished = true)
//            } else item
//        }
//        if (!isNotFinished) {
//            setOnBoardingList()
//        }
//    }

//    //TODO 진행 중인 강의를 update 하는
//    private fun updateLessonItemCount(count: Int, lessonId: Int) {
//        if (count == 1) {
//            increaseLessonItemCount(lessonId)
//        } else {
//            decreaseLessonItemCount(lessonId)
//        }
//        // refreshLessonItem(lessonId)
//    }

//    //TODO lessonUiModel 이라 todayLessonResponse 내부 원소 접근 불가능
//    private fun increaseLessonItemCount(lessonId: Int) {
//        _todayLessons.update { list ->
//            list.map { item ->
//                if (item.lessonId == lessonId) {
//                    val isOutOfRange = item.presentNumber == item.totalNumber
//                    if (isOutOfRange) return
//                    item.copy(presentNumber = item.presentNumber.plus(1))
//                } else item
//            }
//        }
//    }

//    private fun decreaseLessonItemCount(lessonId: Int) {
//        _todayLessons.update { list ->
//            list.map { item ->
//                if (item.lessonId == lessonId) {
//                    val isOutOfRange = item.presentNumber == 0
//                    if (isOutOfRange) return
//                    item.copy(presentNumber = item.presentNumber.minus(1))
//                } else item
//            }
//        }
//    }

//    // TODO 갱신 경우의 수 보강
//    private fun refreshLessonItem(lessonId: Int) {
//        _todayLessons.update { list ->
//            list.map { item ->
//                if (item.lessonId == lessonId) {
//                    val isTodayFinished = item.presentNumber >= item.untilTodayNumber
//                    if (isTodayFinished) {
//                        item.copy(untilTodayFinished = true)
//                    } else {
//                        item.copy(untilTodayFinished = false)
//                    }
//                } else item
//            }
//        }
//
//        // 우선은 갱신을 위해 api 를 재호출 하는 방식으로 구현
//        // fetchTodayLessonList()
//    }

    fun navigateToOnBoardingLessonRegisterDialog() = viewModelScope.launch {
        _navigateToOnBoardingRegisterLessonDialogEvent.emit(Unit)
    }

    fun fetchTodayLessonList() {
        todayLessonJob.cancelIfActive()
        todayLessonJob = viewModelScope.launch {
            fetchLessonTodayListUseCase().onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        setInternetError(false)
                        setLessonList(result.data)
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

    private fun setLessonList(result: List<TodayLessonResponse>) {
        _todayLessonList.value = if (result.isEmpty()) {
            listOf(
                TodayLessonUiModel.TodayLessonHeaderItem(TodayLessonType.EMPTY),
                TodayLessonUiModel.TodayLessonTitleItem(TodayLessonType.EMPTY),
                TodayLessonUiModel.TodayLessonEmptyItem
            )
        } else {
            result.groupBy {
                it.untilTodayFinished
            }.values.map { todayLessonList ->
                todayLessonList.map { lesson ->
                    if (lesson.untilTodayFinished) {
                        TodayLessonUiModel.TodayLessonFinishedItem(lesson)
                    } else {
                        TodayLessonUiModel.TodayLessonDoingItem(lesson)
                    }
                }
                //flatMap 때문일 수도 있음
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
                val isNotStarted = lessons.filterIsInstance<TodayLessonUiModel.TodayLessonDoingItem>().all { it.todayLesson.presentNumber == 0 }
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

    //TODO isNotStarted 상태와 isDoing 상태를 구분하기 위해 하나라도 0 이 아닌게 있으면 새로고침을 해줘야한다
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
                            // DoingLesson
                            if (!lesson.untilTodayFinished) {
                                if (result.data.presentNumber == lesson.untilTodayNumber) {
                                    // refresh
                                    // TODO 어쨌든 fetch 를 통해 refresh 를 수행하면 미세하게 깜빡거리는 현상은 생길 수 밖에 없다.
                                    fetchTodayLessonList()
                                } else {
                                    // updateLessonItemCount(count, lesson.lessonId)
                                }
                            }
                            // FinishLesson
                            else {
                                if (result.data.presentNumber < lesson.untilTodayNumber ||
                                    result.data.presentNumber == lesson.totalNumber ||
                                    count == -1 && result.data.presentNumber + 1 == lesson.totalNumber
                                ) {
                                    // refresh
                                    fetchTodayLessonList()
                                } else {
                                    // updateLessonItemCount(count, lesson.lessonId)
                                }
                            }
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
