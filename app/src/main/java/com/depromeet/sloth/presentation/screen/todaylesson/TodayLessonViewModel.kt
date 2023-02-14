package com.depromeet.sloth.presentation.screen.todaylesson

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonTodayResponse
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.usecase.lesson.FetchTodayLessonListUseCase
import com.depromeet.sloth.domain.usecase.lesson.UpdateLessonCountUseCase
import com.depromeet.sloth.domain.usecase.login.FetchLoginStatusUseCase
import com.depromeet.sloth.domain.usecase.member.FetchOnBoardingStatusUseCase
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
    private val fetchOnBoardingStatusUseCase: FetchOnBoardingStatusUseCase,
    private val fetchLessonTodayListUseCase: FetchTodayLessonListUseCase,
    private val updateLessonCountUseCase: UpdateLessonCountUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
) : BaseViewModel() {

    private val _autoLoginEvent = MutableSharedFlow<Boolean>(replay = 1)
    val autoLoginEvent: SharedFlow<Boolean> = _autoLoginEvent.asSharedFlow()

    private val _checkOnBoardingCompleteEvent = MutableSharedFlow<Boolean>(1)
    val checkOnBoardingCompleteEvent: SharedFlow<Boolean> =
        _checkOnBoardingCompleteEvent.asSharedFlow()

    private val _onBoardingComplete = MutableStateFlow<Boolean>(false)
    val onBoardingComplete: StateFlow<Boolean> = _onBoardingComplete.asStateFlow()

    private val _showOnBoardingClickPlusEvent = MutableSharedFlow<Unit>()
    val showOnBoardingClickPlusEvent: SharedFlow<Unit> =
        _showOnBoardingClickPlusEvent.asSharedFlow()

    private val _showOnBoardingRegisterLessonEvent = MutableSharedFlow<Unit>()
    val showOnBoardingRegisterLessonEvent: SharedFlow<Unit> =
        _showOnBoardingRegisterLessonEvent.asSharedFlow()

    private val _lessonEmptyList = MutableStateFlow(emptyList<LessonTodayResponse>())
    val lessonEmptyList: StateFlow<List<LessonTodayResponse>> = _lessonEmptyList.asStateFlow()

    private val _lessonDoingList = MutableStateFlow(emptyList<LessonTodayResponse>())
    val lessonDoingList: StateFlow<List<LessonTodayResponse>> =
        _lessonDoingList.asStateFlow()

    private val _lessonFinishedList = MutableStateFlow(emptyList<LessonTodayResponse>())
    val lessonFinishedList: StateFlow<List<LessonTodayResponse>> = _lessonFinishedList.asStateFlow()

//    private val _onBoardingList = MutableStateFlow<List<LessonTodayResponse>>(
//        mutableListOf(
//            LessonTodayResponse(
//                "튜토리얼",
//                0,
//                "나공이와 대결하기\n: 게이지를 빨리 채워 완료해봐요!",
//                0,
//                1,
//                "",
//                false,
//                3,
//                3
//            )
//        )
//    )
//    val onBoardingList: StateFlow<List<LessonTodayResponse>> =
//        _onBoardingList.asStateFlow()

    private val _onBoardingDoingList = MutableStateFlow<List<LessonTodayResponse>>(
        mutableListOf(
            LessonTodayResponse(
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
    val onBoardingDoingList: StateFlow<List<LessonTodayResponse>> =
        _onBoardingDoingList.asStateFlow()

    private val _onBoardingFinishedList = MutableStateFlow(emptyList<LessonTodayResponse>())
    val onBoardingFinishedList: StateFlow<List<LessonTodayResponse>> =
        _onBoardingFinishedList.asStateFlow()

    init {
        checkLoginStatus()
    }

    private val _fetchLessonListSuccessEvent = MutableSharedFlow<Unit>()
    val fetchLessonListSuccessEvent: SharedFlow<Unit> =
        _fetchLessonListSuccessEvent.asSharedFlow()

    private val _navigateToRegisterLessonEvent = MutableSharedFlow<Unit>()
    val navigateRegisterLessonEvent: SharedFlow<Unit> =
        _navigateToRegisterLessonEvent.asSharedFlow()

    private val _navigateToWaitDialogEvent = MutableSharedFlow<Unit>()
    val navigateToWaitDialogEvent: SharedFlow<Unit> = _navigateToWaitDialogEvent.asSharedFlow()

    private fun checkLoginStatus() = viewModelScope.launch {
        _autoLoginEvent.emit(fetchLoginStatusUseCase())
    }

    fun checkOnBoardingComplete() = viewModelScope.launch {
        _checkOnBoardingCompleteEvent.emit(fetchOnBoardingStatusUseCase())
    }

    fun updateOnBoardingStatus() {
        _onBoardingComplete.value = true
    }

//    private fun setOnBoardingList() {
//        if (onBoardingList.value.isNotEmpty()) {
//            onBoardingList.value.forEach { lesson ->
//                if (lesson.untilTodayFinished) {
//                    _onBoardingFinishedList.value =
//                        lessonFinishedList.value.toMutableList().apply { add(lesson) }
//                } else {
//                    _onBoardingDoingList.value =
//                        lessonDoingList.value.toMutableList().apply { add(lesson) }
//                }
//            }
//        } else {
//            _lessonEmptyList.value = lessonEmptyList.value.toMutableList().apply {
//                add(LessonTodayResponse.EMPTY)
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

    //TODO 진행 중인 강의를 update 하는
    private fun updateLessonItemCount(count: Int, lessonId: Int) {
        if (count == 1) {
            increaseLessonItemCount(lessonId)
        } else {
            decreaseLessonItemCount(lessonId)
        }
        refreshLessonItem(lessonId)
    }

    //TODO finsihedLessonList에도 적용
    private fun increaseLessonItemCount(lessonId: Int) {
        _lessonDoingList.value = lessonDoingList.value.map { item ->
            if (item.lessonId == lessonId) {
                val isOutOfRange = item.presentNumber == item.totalNumber
                if (isOutOfRange) return
                item.copy(presentNumber = item.presentNumber.plus(1))
            } else item
        }
    }

    private fun decreaseLessonItemCount(lessonId: Int) {
        _lessonDoingList.value = lessonDoingList.value.map { item ->
            if (item.lessonId == lessonId) {
                val isOutOfRange = item.presentNumber == 0
                if (isOutOfRange) return
                item.copy(presentNumber = item.presentNumber.minus(1))
            } else item
        }
    }

    private fun refreshLessonItem(lessonId: Int) {
//        _lessonDoingList.value = lessonDoingList.value.map { item ->
//            if (item.lessonId == lessonId) {
//                val isTodayFinished = item.presentNumber >= item.untilTodayNumber
//                if (isTodayFinished) {
//                    item.copy(untilTodayFinished = true)
//                    // TODO lessonFinishedLesson 쪽으로 item 이동
//                } else {
//                    item.copy(untilTodayFinished = false)
//                }
//            } else item
//        }

        // 우선은 갱신을 위해 api 를 재호출 하는 방식으로 구현
        fetchTodayLessonList()
    }

    fun showOnBoardingClickPlus() = viewModelScope.launch {
        _showOnBoardingClickPlusEvent.emit(Unit)
    }

    fun showOnBoardingLessonRegister() = viewModelScope.launch {
        _showOnBoardingRegisterLessonEvent.emit(Unit)
    }

    //TODO OnBoarding Validation 추가
    fun fetchTodayLessonList() = viewModelScope.launch {
        fetchLessonTodayListUseCase()
            .onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        setInternetError(false)
                        setTodayLessonList(result.data)
                        _fetchLessonListSuccessEvent.emit(Unit)
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

    //TODO update function 을 이용 해야 할듯
    private fun setTodayLessonList(todayLessonList: List<LessonTodayResponse>) {
        if (todayLessonList.isNotEmpty()) {
            todayLessonList.forEach { lesson ->
                if (lesson.untilTodayFinished) {
                    _lessonFinishedList.value =
                        lessonFinishedList.value.toMutableList().apply { add(lesson) }
                } else {
                    _lessonDoingList.value =
                        lessonDoingList.value.toMutableList().apply { add(lesson) }
                }
            }
        } else {
            _lessonEmptyList.value = lessonEmptyList.value.toMutableList().apply {
                add(LessonTodayResponse.EMPTY)
            }
        }
    }

    fun updateLessonCount(count: Int, lessonId: Int) = viewModelScope.launch {
        updateLessonCountUseCase(count = count, lessonId = lessonId)
            .onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        updateLessonItemCount(count, lessonId)
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

    override fun retry() {
        fetchTodayLessonList()
    }
}
