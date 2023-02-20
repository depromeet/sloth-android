package com.depromeet.sloth.presentation.screen.onboarding

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.usecase.member.FetchTodayLessonOnBoardingStatusUseCase
import com.depromeet.sloth.domain.usecase.member.UpdateTodayLessonOnBoardingStatusUseCase
import com.depromeet.sloth.presentation.screen.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OnBoardingTodayLessonViewModel @Inject constructor(
    private val fetchTodayLessonOnBoardingStatusUseCase: FetchTodayLessonOnBoardingStatusUseCase,
    private val updateTodayLessonOnBoardingStatusUseCase: UpdateTodayLessonOnBoardingStatusUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
) : BaseViewModel() {

    private val _checkTodayLessonOnBoardingCompleteEvent = MutableSharedFlow<Boolean>(1)
    val checkTodayLessonOnBoardingCompleteEvent: SharedFlow<Boolean> = _checkTodayLessonOnBoardingCompleteEvent.asSharedFlow()

    private val _todayLessonOnBoardingComplete = MutableStateFlow(false)
    val todayLessonOnBoardingComplete: StateFlow<Boolean> = _todayLessonOnBoardingComplete.asStateFlow()

    private val _onBoardingList = MutableStateFlow(emptyList<OnBoardingUiModel>())
    val onBoardingList: StateFlow<List<OnBoardingUiModel>> = _onBoardingList.asStateFlow()

    private val _navigateToOnBoardingClickPlusDialogEvent = MutableSharedFlow<Unit>()
    val navigateToOnBoardingClickPlusDialogEvent: SharedFlow<Unit> = _navigateToOnBoardingClickPlusDialogEvent.asSharedFlow()

    private val _navigateToOnBoardingRegisterLessonDialogEvent = MutableSharedFlow<Unit>()
    val navigateToOnBoardingRegisterLessonDialogEvent: SharedFlow<Unit> = _navigateToOnBoardingRegisterLessonDialogEvent.asSharedFlow()

    private val _navigateToRegisterLessonEvent = MutableSharedFlow<Unit>()
    val navigateToRegisterLessonEvent: SharedFlow<Unit> = _navigateToRegisterLessonEvent.asSharedFlow()

    init {
        navigateToOnBoardingClickPlusDialog()
    }

    private fun navigateToOnBoardingClickPlusDialog() = viewModelScope.launch {
        _navigateToOnBoardingClickPlusDialogEvent.emit(Unit)
    }

    fun checkTodayLessonOnBoardingComplete() = viewModelScope.launch {
        _checkTodayLessonOnBoardingCompleteEvent.emit(fetchTodayLessonOnBoardingStatusUseCase())
    }

    fun updateTodayLessonOnBoardingStatus() {
        _todayLessonOnBoardingComplete.value = true
    }

    fun fetchOnBoardingTodayLessonList() {
        _onBoardingList.update {
            listOf(
                OnBoardingUiModel.OnBoardingHeaderItem(OnBoardingType.DOING),
                OnBoardingUiModel.OnBoardingTitleItem(OnBoardingType.DOING),
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

    // TODO update function 을 이용 해야 할듯
    // 사실 race condition 일어날 조건은 아니긴 함
    fun updateOnBoardingItemCount(count: Int) {
        if (count == 1) {
            increaseOnBoardingItemCount()
        } else {
            decreaseOnBoardingItemCount()
        }
        refreshOnBoardingItem()
    }

    private fun increaseOnBoardingItemCount() {
        val currentList = _onBoardingList.value.toMutableList()
        for (i in currentList.indices) {
            if (currentList[i] is OnBoardingUiModel.OnBoardingDoingItem) {
                val currentTodayLesson = (currentList[i] as OnBoardingUiModel.OnBoardingDoingItem).todayLesson
                val isOutOfRange = currentTodayLesson.presentNumber == currentTodayLesson.untilTodayNumber
                if (isOutOfRange) return
                val updateTodayLesson = currentTodayLesson.copy(presentNumber = currentTodayLesson.presentNumber + 1)
                currentList[i] = (currentList[i] as OnBoardingUiModel.OnBoardingDoingItem).copy(todayLesson = updateTodayLesson)
                break
            }
        }
        currentList.map {
            when (it) {
                is OnBoardingUiModel.OnBoardingDoingItem -> {
                    if (it.todayLesson.presentNumber == it.todayLesson.untilTodayNumber) {
                        it.copy(todayLesson = it.todayLesson.copy(untilTodayFinished = true))
                    } else {
                        it
                    }
                }
                else -> it
            }
        }
        _onBoardingList.value = currentList
    }

    private fun decreaseOnBoardingItemCount() {
        val currentList = _onBoardingList.value.toMutableList()
        for (i in currentList.indices) {
            if (currentList[i] is OnBoardingUiModel.OnBoardingDoingItem) {
                val currentTodayLesson = (currentList[i] as OnBoardingUiModel.OnBoardingDoingItem).todayLesson
                val isOutOfRange = currentTodayLesson.presentNumber == 0
                if (isOutOfRange) return
                val updateTodayLesson = currentTodayLesson.copy(presentNumber = currentTodayLesson.presentNumber - 1)
                currentList[i] = (currentList[i] as OnBoardingUiModel.OnBoardingDoingItem).copy(todayLesson = updateTodayLesson)
                break
            }
        }
        currentList.map {
            when (it) {
                is OnBoardingUiModel.OnBoardingDoingItem -> {
                    if (it.todayLesson.presentNumber < it.todayLesson.untilTodayNumber) {
                        it.copy(todayLesson = it.todayLesson.copy(untilTodayFinished = false))
                    } else {
                        it
                    }
                }
                else -> it
            }
        }
        _onBoardingList.value = currentList
    }

    //TODO Header, Title 다 바꿔줘야 한다...
    private fun refreshOnBoardingItem() {
        val currentList = _onBoardingList.value.toMutableList()
        currentList.map {
            when (it) {
                is OnBoardingUiModel.OnBoardingDoingItem -> {
                    if (it.todayLesson.untilTodayFinished) {
                        OnBoardingUiModel.OnBoardingFinishedItem(it.todayLesson)
                        // 로그 안찍힘
                    } else {
                        it
                    }
                }
                is OnBoardingUiModel.OnBoardingFinishedItem -> {
                    if (!it.todayLesson.untilTodayFinished) {
                        OnBoardingUiModel.OnBoardingDoingItem(it.todayLesson)
                        // 로그 안찍힘
                    } else {
                        it
                    }
                }
                else -> it
            }
        }
        _onBoardingList.value = currentList
    }

    fun navigateToOnBoardingLessonRegisterDialog() = viewModelScope.launch {
        // list 초기화
        initOnBoardingList()
        _navigateToOnBoardingRegisterLessonDialogEvent.emit(Unit)
    }

    private fun initOnBoardingList() {
        _onBoardingList.update { emptyList() }
    }

    fun navigateToRegisterLesson() = viewModelScope.launch {
        _navigateToRegisterLessonEvent.emit(Unit)
    }

    override fun retry() = Unit
}
