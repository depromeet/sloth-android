package com.depromeet.presentation.ui.onboarding

import androidx.lifecycle.viewModelScope
import com.depromeet.domain.usecase.userprofile.UpdateTodayLessonOnBoardingStatusUseCase
import com.depromeet.presentation.model.TodayLesson
import com.depromeet.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OnBoardingTodayLessonViewModel @Inject constructor(
    private val updateTodayLessonOnBoardingStatusUseCase: UpdateTodayLessonOnBoardingStatusUseCase,
) : BaseViewModel() {

    private var onBoardingList = listOf(TodayLesson(0, "", false, 0, "", "", 0, 3, 3))

    private val _onBoardingUiModelList = MutableStateFlow(emptyList<OnBoardingUiModel>())
    val onBoardingUiModelList: StateFlow<List<OnBoardingUiModel>> = _onBoardingUiModelList.asStateFlow()

    private val _navigateToOnBoardingClickPlusDialogEvent = MutableSharedFlow<Unit>(replay = 1)
    val navigateToOnBoardingClickPlusDialogEvent: SharedFlow<Unit> = _navigateToOnBoardingClickPlusDialogEvent.asSharedFlow()

    private val _navigateToOnBoardingRegisterLessonDialogEvent = MutableSharedFlow<Unit>()
    val navigateToOnBoardingRegisterLessonDialogEvent: SharedFlow<Unit> = _navigateToOnBoardingRegisterLessonDialogEvent.asSharedFlow()

    private val _navigateToRegisterLessonEvent = MutableSharedFlow<Int>()
    val navigateToRegisterLessonEvent: SharedFlow<Int> = _navigateToRegisterLessonEvent.asSharedFlow()

    init {
        navigateToOnBoardingClickPlusDialog()
        setOnBoardingItem()
    }

    private fun navigateToOnBoardingClickPlusDialog() = viewModelScope.launch {
        _navigateToOnBoardingClickPlusDialogEvent.emit(Unit)
    }

    fun updateOnBoardingItemCount(count: Int) {
        if (count == 1) {
            increaseOnBoardingItemCount()
        } else {
            decreaseOnBoardingItemCount()
        }
    }

    private fun increaseOnBoardingItemCount() {
        lateinit var newItem: TodayLesson
        var flag = false

        val currentList = onBoardingList.toMutableList()
        val item = onBoardingList.first()

        val isFinished = item.presentNumber + 1 == item.untilTodayNumber
        if (isFinished) {
            newItem = item.copy(presentNumber = item.presentNumber + 1, untilTodayFinished = true)
            flag = true
        } else {
            newItem = item.copy(presentNumber = item.presentNumber + 1)
        }
        currentList[0] = newItem
        onBoardingList = currentList
        if (flag) setOnBoardingItem()
    }

    private fun decreaseOnBoardingItemCount() {
        lateinit var newItem: TodayLesson
        var flag = false

        val currentList = onBoardingList.toMutableList()
        val item = currentList.first()
        val isOutOfRange = item.presentNumber <= 0
        if (isOutOfRange) return

        val isNotFinished = item.presentNumber == item.untilTodayNumber
        if (isNotFinished) {
            newItem = item.copy(presentNumber = item.presentNumber - 1, untilTodayFinished = false)
            flag = true
        } else {
            newItem = item.copy(presentNumber = item.presentNumber - 1)
        }

        currentList[0] = newItem
        onBoardingList = currentList
        if (flag) setOnBoardingItem()
    }

    private fun setOnBoardingItem() {
        _onBoardingUiModelList.update {
            if (onBoardingList.isEmpty()) {
                listOf(
                    OnBoardingUiModel.OnBoardingHeaderItem(OnBoardingType.EMPTY),
                    OnBoardingUiModel.OnBoardingTitleItem(OnBoardingType.EMPTY),
                    OnBoardingUiModel.OnBoardingEmptyItem
                )
            } else {
                onBoardingList.groupBy {
                    it.untilTodayFinished
                }.values.map {
                    it.map { lesson ->
                        if (lesson.untilTodayFinished) {
                            OnBoardingUiModel.OnBoardingFinishedItem(lesson)
                        } else {
                            OnBoardingUiModel.OnBoardingDoingItem(lesson)
                        }
                    }
                }.flatMap {
                    when (it.first()) {
                        is OnBoardingUiModel.OnBoardingDoingItem -> {
                            it.toMutableList().apply {
                                add(0, OnBoardingUiModel.OnBoardingTitleItem(OnBoardingType.DOING))
                            }
                        }
                        is OnBoardingUiModel.OnBoardingFinishedItem -> {
                            it.toMutableList().apply {
                                add(0, OnBoardingUiModel.OnBoardingTitleItem(OnBoardingType.FINISHED))
                            }
                        }
                        else -> return
                    }
                }.let {
                    val isFinished = it.find { it is OnBoardingUiModel.OnBoardingDoingItem } == null
                    when {
                        isFinished -> {
                            it.toMutableList().apply {
                                add(0, OnBoardingUiModel.OnBoardingHeaderItem(OnBoardingType.FINISHED))
                            }
                        }
                        else -> {
                            it.toMutableList().apply {
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
        onBoardingList = emptyList()
        setOnBoardingItem()
    }

    fun navigateToRegisterLesson(fragmentId: Int) = viewModelScope.launch {
        updateOnBoardingTodayLessonStatus(true)
        _navigateToRegisterLessonEvent.emit(fragmentId)
    }

    private fun updateOnBoardingTodayLessonStatus(flag: Boolean) = viewModelScope.launch {
        updateTodayLessonOnBoardingStatusUseCase(flag)
    }

    override fun retry() = Unit
}
