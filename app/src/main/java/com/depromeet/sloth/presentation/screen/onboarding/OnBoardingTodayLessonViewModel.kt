package com.depromeet.sloth.presentation.screen.onboarding

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse
import com.depromeet.sloth.domain.usecase.member.UpdateTodayLessonOnBoardingStatusUseCase
import com.depromeet.sloth.presentation.screen.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


//TODO 온보딩 백스택 핸들링
@HiltViewModel
class OnBoardingTodayLessonViewModel @Inject constructor(
    private val updateTodayLessonOnBoardingStatusUseCase: UpdateTodayLessonOnBoardingStatusUseCase,
) : BaseViewModel() {

    private val _onBoardingList = MutableStateFlow(
        listOf(TodayLessonResponse(0, "나공이와 대결하기\n: 게이지를 빨리 채워 완료해봐요!", false, 1, "", "", 0, 3, 3))
    )
    val onBoardingList: StateFlow<List<TodayLessonResponse>> = _onBoardingList.asStateFlow()

    private val _onBoardingUiModelList = MutableStateFlow(emptyList<OnBoardingUiModel>())
    val onBoardingUiModelList: StateFlow<List<OnBoardingUiModel>> = _onBoardingUiModelList.asStateFlow()

    private val _navigateToOnBoardingClickPlusDialogEvent = MutableSharedFlow<Unit>(replay = 1)
    val navigateToOnBoardingClickPlusDialogEvent: SharedFlow<Unit> = _navigateToOnBoardingClickPlusDialogEvent.asSharedFlow()

    private val _navigateToOnBoardingRegisterLessonDialogEvent = MutableSharedFlow<Unit>()
    val navigateToOnBoardingRegisterLessonDialogEvent: SharedFlow<Unit> = _navigateToOnBoardingRegisterLessonDialogEvent.asSharedFlow()

    private val _navigateToRegisterLessonEvent = MutableSharedFlow<Unit>()
    val navigateToRegisterLessonEvent: SharedFlow<Unit> = _navigateToRegisterLessonEvent.asSharedFlow()

    init {
        navigateToOnBoardingClickPlusDialog()
        setOnBoardingItem()
    }

    private fun navigateToOnBoardingClickPlusDialog() = viewModelScope.launch {
        _navigateToOnBoardingClickPlusDialogEvent.emit(Unit)
    }

    // TODO update function 을 이용 해야 할듯
    // 사실 race condition 일어날 조건은 아니긴 함
    fun updateOnBoardingItemCount(count: Int) {
        if (count == 1) {
            increaseOnBoardingItemCount()
        } else {
            decreaseOnBoardingItemCount()
        }
    }

    private fun increaseOnBoardingItemCount() {
        lateinit var newItem: TodayLessonResponse
        var flag = false

        val currentList = _onBoardingList.value.toMutableList()
        for ((index, item) in currentList.withIndex()) {
            val isOutOfRange = item.presentNumber == item.totalNumber
            if (isOutOfRange) return

            val isFinished = item.presentNumber + 1 == item.untilTodayNumber
            if (isFinished) {
                newItem = item.copy(presentNumber = item.presentNumber + 1, untilTodayFinished = true)
                flag = true
            } else {
                newItem = item.copy(presentNumber = item.presentNumber + 1)
            }
            currentList[index] = newItem
            _onBoardingList.value = currentList
            if (flag) {
                setOnBoardingItem()
            }
        }
    }

    private fun decreaseOnBoardingItemCount() {
        lateinit var newItem: TodayLessonResponse
        var flag = false

        val currentList = _onBoardingList.value.toMutableList()
        for ((index, item) in currentList.withIndex()) {
            val isOutOfRange = item.presentNumber == 0
            if (isOutOfRange) return

            val isNotFinished = item.presentNumber == item.untilTodayNumber
            if (isNotFinished) {
                newItem = item.copy(presentNumber = item.presentNumber - 1, untilTodayFinished = false)
                flag = true
            } else {
                newItem = item.copy(presentNumber = item.presentNumber - 1)
            }
            currentList[index] = newItem
            _onBoardingList.value = currentList
            if (flag) {
                setOnBoardingItem()
            }
        }
    }

    private fun setOnBoardingItem() {
        _onBoardingUiModelList.update {
            if (_onBoardingList.value.isEmpty()) {
                listOf(
                    OnBoardingUiModel.OnBoardingHeaderItem(OnBoardingType.EMPTY),
                    OnBoardingUiModel.OnBoardingTitleItem(OnBoardingType.EMPTY),
                    OnBoardingUiModel.OnBoardingEmptyItem
                )
            } else {
                onBoardingList.value.groupBy {
                    it.untilTodayFinished
                }.values.map { todayLessonList ->
                    todayLessonList.map { lesson ->
                        if (lesson.untilTodayFinished) {
                            OnBoardingUiModel.OnBoardingFinishedItem(lesson)
                        } else {
                            OnBoardingUiModel.OnBoardingDoingItem(lesson)
                        }
                    }
                }.flatMap { todayLessons ->
                    when (todayLessons.first()) {
                        is OnBoardingUiModel.OnBoardingDoingItem -> {
                            todayLessons.toMutableList().apply {
                                add(0, OnBoardingUiModel.OnBoardingTitleItem(OnBoardingType.DOING))
                            }
                        }
                        is OnBoardingUiModel.OnBoardingFinishedItem -> {
                            todayLessons.toMutableList().apply {
                                add(0, OnBoardingUiModel.OnBoardingTitleItem(OnBoardingType.FINISHED))
                            }
                        }
                        else -> return
                    }
                }.let { lessons ->
                    val isFinished = lessons.find { it is OnBoardingUiModel.OnBoardingDoingItem } == null
                    when {
                        isFinished -> {
                            lessons.toMutableList().apply {
                                add(0, OnBoardingUiModel.OnBoardingHeaderItem(OnBoardingType.FINISHED))
                            }
                        }
                        else -> {
                            lessons.toMutableList().apply {
                                add(0, OnBoardingUiModel.OnBoardingHeaderItem(OnBoardingType.DOING))
                            }
                        }
                    }
                }
            }
        }
    }

    fun navigateToOnBoardingLessonRegisterDialog() = viewModelScope.launch {
        initOnBoardingList()
        _navigateToOnBoardingRegisterLessonDialogEvent.emit(Unit)
    }

    private fun initOnBoardingList() {
        _onBoardingList.update { emptyList() }
        setOnBoardingItem()
    }

    fun navigateToRegisterLesson() = viewModelScope.launch {
        updateOnBoardingTodayLessonStatus()
        _navigateToRegisterLessonEvent.emit(Unit)
    }

    private fun updateOnBoardingTodayLessonStatus() = viewModelScope.launch {
        updateTodayLessonOnBoardingStatusUseCase()
    }

    override fun retry() = Unit
}
