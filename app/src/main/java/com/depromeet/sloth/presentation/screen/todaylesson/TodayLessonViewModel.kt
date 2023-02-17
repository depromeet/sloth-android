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
import com.depromeet.sloth.presentation.adapter.HeaderAdapter
import com.depromeet.sloth.presentation.screen.LessonUiModel
import com.depromeet.sloth.presentation.screen.base.BaseViewModel
import com.depromeet.sloth.util.INTERNET_CONNECTION_ERROR
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodayLessonViewModel @Inject constructor(
    private val fetchLoginStatusUseCase: FetchLoginStatusUseCase,
    private val fetchTodayLessonOnBoardingStatusUseCase: FetchTodayLessonOnBoardingStatusUseCase,
    private val updateTodayLessonOnBoardingStatusUseCase: UpdateTodayLessonOnBoardingStatusUseCase,
    private val fetchLessonTodayListUseCase: FetchTodayLessonListUseCase,
    private val updateLessonCountUseCase: UpdateLessonCountUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
) : BaseViewModel() {

    private val _autoLoginEvent = MutableSharedFlow<Boolean>(replay = 1)
    val autoLoginEvent: SharedFlow<Boolean> = _autoLoginEvent.asSharedFlow()

//    private val _checkOnBoardingCompleteEvent = MutableSharedFlow<Boolean>(1)
//    val checkOnBoardingCompleteEvent: SharedFlow<Boolean> =
//        _checkOnBoardingCompleteEvent.asSharedFlow()

//    private val _onBoardingComplete = MutableStateFlow<Boolean>(false)
//    val onBoardingComplete: StateFlow<Boolean> = _onBoardingComplete.asStateFlow()
//
//    private val _showOnBoardingClickPlusEvent = MutableSharedFlow<Unit>()
//    val showOnBoardingClickPlusEvent: SharedFlow<Unit> =
//        _showOnBoardingClickPlusEvent.asSharedFlow()
//
//    private val _showOnBoardingRegisterLessonEvent = MutableSharedFlow<Unit>()
//    val showOnBoardingRegisterLessonEvent: SharedFlow<Unit> =
//        _showOnBoardingRegisterLessonEvent.asSharedFlow()


    private val _onBoardingList = MutableStateFlow(emptyList<LessonUiModel>())
    val onBoardingList: StateFlow<List<LessonUiModel>> = _onBoardingList.asStateFlow()

//    //TODO LessonUiModel 로 wrapping 되어서 뷰모델내에서 직접 맴버 변수에 접근하여 상태를 변경시키지 못하는 이슈
//    // 분리해보자 <- SSOT 위배
//    private val _todayLessons = MutableStateFlow(emptyList<TodayLessonResponse>())
//    val todayLessons: StateFlow<List<TodayLessonResponse>> = _todayLessons.asStateFlow()

    private val _todayLessonList = MutableStateFlow(emptyList<LessonUiModel>())
    val todayLessonList: StateFlow<List<LessonUiModel>> = _todayLessonList.asStateFlow()

    init {
        checkLoginStatus()
    }

    private val _navigateToRegisterLessonEvent = MutableSharedFlow<Unit>()
    val navigateRegisterLessonEvent: SharedFlow<Unit> =
        _navigateToRegisterLessonEvent.asSharedFlow()

    private val _navigateToFinishLessonDialogEvent = MutableSharedFlow<String>()
    val navigateToFinishLessonDialogEvent: SharedFlow<String> =
        _navigateToFinishLessonDialogEvent.asSharedFlow()

    private val _navigateToWaitDialogEvent = MutableSharedFlow<Unit>()
    val navigateToWaitDialogEvent: SharedFlow<Unit> = _navigateToWaitDialogEvent.asSharedFlow()

    private fun checkLoginStatus() = viewModelScope.launch {
        _autoLoginEvent.emit(fetchLoginStatusUseCase())
    }

//    fun checkTodayLessonOnBoardingComplete() = viewModelScope.launch {
//        _checkTodayLessonOnBoardingCompleteEvent.emit(fetchTodayLessonOnBoardingStatusUseCase())
//    }

//    fun updateTodayLessonOnBoardingStatus() {
//        _todayLessonOnBoardingComplete.value = true
//    }

//    private fun setOnBoardingList() {
//        _onBoardingList.update { list ->
//            list.toMutableList().apply {
//                add(
//                    LessonUiModel.OnBoardingDoingLesson(
//                        TodayLessonResponse(
//                            "튜토리얼",
//                            0,
//                            "나공이와 대결하기\n: 게이지를 빨리 채워 완료해봐요!",
//                            0,
//                            1,
//                            "",
//                            false,
//                            3,
//                            3
//                        )
//                    )
//                )
//            }
//        }
//    }

    // TODO update function 을 이용 해야 할듯
    // 사실 race condition 일어날 조건은 아니긴 함
//    fun updateOnBoardingItemCount(count: Int) {
//        if (count == 1) {
//            increaseOnBoardingItemCount()
//        } else {
//            decreaseOnBoardingItemCount()
//        }
//        refreshOnBoardingItem()
//    }
//
//    private fun increaseOnBoardingItemCount() {
//        _onBoardingDoingList.value = onBoardingDoingList.value.mapIndexed { index, item ->
//            if (index == 0) {
//                val isOutOfRange = item.presentNumber == item.totalNumber
//                if (isOutOfRange) return
//                item.copy(presentNumber = item.presentNumber.plus(1))
//            } else item
//        }
//    }

//    private fun decreaseOnBoardingItemCount() {
//        _onBoardingDoingList.value = onBoardingDoingList.value.mapIndexed { index, item ->
//            if (index == 0) {
//                val isOutOfRange = item.presentNumber == 0
//                if (isOutOfRange) return
//                item.copy(presentNumber = item.presentNumber.minus(1))
//            } else item
//        }
//    }

//    private fun refreshOnBoardingItem() {
//        var isNotFinished = true
//        _onBoardingDoingList.value = onBoardingDoingList.value.mapIndexed { index, item ->
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

//    fun navigateToOnBoardingClickPlus() = viewModelScope.launch {
//        _navigateToOnBoardingClickPlusEvent.emit(Unit)
//    }
//
//    fun navigateTonBoardingLessonRegister() = viewModelScope.launch {
//        _navigateToOnBoardingRegisterLessonEvent.emit(Unit)
//    }

    //TODO OnBoarding Validation 추가
    fun fetchTodayLessonList() = viewModelScope.launch {
//        val isOnBoardingComplete = fetchTodayLessonOnBoardingStatusUseCase()
//        if (isOnBoardingComplete) {
        fetchLessonTodayListUseCase()
            .onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        setInternetError(false)
                        setTodayLessonList(result.data)
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
//        } else {
//            setOnBoardingList()
//        }
    }

    private fun setTodayLessonList(result: List<TodayLessonResponse>) {
        // _todayLessons.update { result }
        _todayLessonList.update {
            if (result.isEmpty()) {
                listOf(
                    LessonUiModel.LessonHeader(HeaderAdapter.HeaderType.EMPTY, null),
                    LessonUiModel.EmptyLesson
                )
            } else {
                // groupBy 가 LessonUiMode 같은 group 이라고 판단 하지 못한다.
                result.groupBy {
                    it.untilTodayFinished
                }.values.map { lessonList ->
                    lessonList.map { lesson ->
                        when {
                            // TODO NotStartLesson 을 판별하는 방법..
                            (lesson.untilTodayFinished) -> LessonUiModel.FinishedLesson(lesson)
                            else -> LessonUiModel.DoingLesson(lesson)
                        }
                    }
                }.map { lessonUiModelList ->
                    lessonUiModelList.toMutableList().apply {
                        add(
                            0,
                            when (lessonUiModelList.first()) {
                                is LessonUiModel.DoingLesson -> LessonUiModel.LessonHeader(
                                    HeaderAdapter.HeaderType.DOING, null
                                )
                                is LessonUiModel.FinishedLesson -> LessonUiModel.LessonHeader(
                                    HeaderAdapter.HeaderType.FINISHED, null
                                )
                                else -> return@apply
                            }
                        )
                    }
                }.flatten()
            }
        }
    }

    fun updateLessonCount(count: Int, lesson: TodayLessonResponse) = viewModelScope.launch {
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
                            if (result.data.presentNumber < lesson.untilTodayNumber || result.data.presentNumber == lesson.totalNumber
                                || result.data.presentNumber + 1 == lesson.totalNumber && count == -1
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
