package com.depromeet.sloth.presentation.screen.lessonlist

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonListResponse
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.usecase.lesson.FetchLessonListUseCase
import com.depromeet.sloth.domain.usecase.member.FetchLessonListOnBoardingStatusUseCase
import com.depromeet.sloth.domain.usecase.member.UpdateLessonListOnBoardingStatusUseCase
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

// TODO 변수 명에 ~list 지양 by Clean Code
@HiltViewModel
class LessonListViewModel @Inject constructor(
    private val fetchLessonListUseCase: FetchLessonListUseCase,
    private val fetchLessonListOnBoardingStatusUseCase: FetchLessonListOnBoardingStatusUseCase,
    private val updateLessonListOnBoardingStatusUseCase: UpdateLessonListOnBoardingStatusUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
) : BaseViewModel() {

    private var lessonListJob: Job? = null

    private val _checkLessonListOnBoardingCompleteEvent = MutableSharedFlow<Boolean>(1)
    val checkLessonListOnBoardingCompleteEvent: SharedFlow<Boolean> =
        _checkLessonListOnBoardingCompleteEvent.asSharedFlow()

    private val _navigateToOnBoardingCheckDetailEvent = MutableSharedFlow<Unit>()
    val navigateToOnBoardingCheckDetailEvent: SharedFlow<Unit> =
        _navigateToOnBoardingCheckDetailEvent.asSharedFlow()

    private val _navigateToRegisterLessonEvent = MutableSharedFlow<Unit>()
    val navigateRegisterLessonEvent: SharedFlow<Unit> =
        _navigateToRegisterLessonEvent.asSharedFlow()

    private val _navigateToNotificationListEvent = MutableSharedFlow<Unit>()
    val navigateToNotificationListEvent: SharedFlow<Unit> =
        _navigateToNotificationListEvent.asSharedFlow()

    private val _navigateToLessonDetailEvent = MutableSharedFlow<String>()
    val navigateToLessonDetailEvent: SharedFlow<String> =
        _navigateToLessonDetailEvent.asSharedFlow()

    private val _lessonList = MutableStateFlow<List<LessonListUiModel>>(emptyList())
    val lessonList: StateFlow<List<LessonListUiModel>> = _lessonList.asStateFlow()

    init {
        checkLessonListOnBoardingComplete()
    }

    //TODO fetchLessonInfo 내에서 onBoarding 이 종료 되었는지 먼저 검사 하고
    // 종료가 되지 않았다면 빠꾸
    // 종료 되었다면 UseCase 를 호출하는 식으로 구현해야 UDF 를 위반하지 않는다
    // TODO stock market app 참고 해서 예외처리 코드 개선
    fun fetchLessonList() = viewModelScope.launch {
        lessonListJob.cancelIfActive()
        fetchLessonListUseCase()
            .onEach { result ->
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

    private fun setLessonList(result: List<LessonListResponse>) {
        _lessonList.update {
            if (result.isEmpty()) {
                listOf(LessonListUiModel.LessonListEmptyItem)
            } else {
                result.groupBy {
                    it.lessonStatus
                }.values.map { lessonList ->
                    lessonList.map { lesson ->
                        when {
                            (lesson.isFinished || lesson.lessonStatus == "PAST") ->
                                LessonListUiModel.LessonListPastItem(lesson)
                            (lesson.lessonStatus == "PLAN") -> LessonListUiModel.LessonListPlanItem(lesson)
                            else -> LessonListUiModel.LessonListCurrentItem(lesson)
                        }
                    }
                }.map { lessonUiModelList ->
                    lessonUiModelList.toMutableList().apply {
                        add(
                            0,
                            when (lessonUiModelList.first()) {
                                is LessonListUiModel.LessonListCurrentItem ->
                                    LessonListUiModel.LessonListTitleItem(LessonListType.CURRENT, this.size)
                                is LessonListUiModel.LessonListPlanItem ->
                                    LessonListUiModel.LessonListTitleItem(LessonListType.PLAN, this.size)
                                is LessonListUiModel.LessonListPastItem ->
                                    LessonListUiModel.LessonListTitleItem(LessonListType.PAST, this.size)
                                else -> return@apply
                            }
                        )
                    }
                }.flatten()
            }
        }
    }

    fun navigateToOnBoardingCheckDetail() = viewModelScope.launch {
        updateOnBoardingLessonListStatus()
        _navigateToOnBoardingCheckDetailEvent.emit(Unit)
    }

    private fun checkLessonListOnBoardingComplete() = viewModelScope.launch {
        _checkLessonListOnBoardingCompleteEvent.emit(fetchLessonListOnBoardingStatusUseCase())
    }

    fun navigateToRegisterLesson() = viewModelScope.launch {
        _navigateToRegisterLessonEvent.emit(Unit)
    }

    fun navigateToNotificationList() = viewModelScope.launch {
        _navigateToNotificationListEvent.emit(Unit)
    }

    fun navigateToLessonDetail(lessonId: String) = viewModelScope.launch {
        _navigateToLessonDetailEvent.emit(lessonId)
    }

    private fun updateOnBoardingLessonListStatus() = viewModelScope.launch {
        updateLessonListOnBoardingStatusUseCase()
    }

    override fun retry() {
        fetchLessonList()
    }
}
