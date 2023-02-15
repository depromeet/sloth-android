package com.depromeet.sloth.presentation.screen.lessonlist

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonListResponse
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.usecase.lesson.FetchLessonListUseCase
import com.depromeet.sloth.domain.usecase.member.FetchOnBoardingStatusUseCase
import com.depromeet.sloth.domain.usecase.member.UpdateOnBoardingStatusUseCase
import com.depromeet.sloth.presentation.adapter.HeaderAdapter
import com.depromeet.sloth.presentation.screen.base.BaseViewModel
import com.depromeet.sloth.util.INTERNET_CONNECTION_ERROR
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonListViewModel @Inject constructor(
    private val fetchLessonListUseCase: FetchLessonListUseCase,
    private val fetchOnBoardingStatusUseCase: FetchOnBoardingStatusUseCase,
    private val updateOnBoardingStatusUseCase: UpdateOnBoardingStatusUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
) : BaseViewModel() {

    private val _checkOnBoardingCompleteEvent = MutableSharedFlow<Boolean>(1)
    val checkOnBoardingCompleteEvent: SharedFlow<Boolean> =
        _checkOnBoardingCompleteEvent.asSharedFlow()

    private val _showOnBoardingCheckDetailEvent = MutableSharedFlow<Unit>()
    val showOnBoardingCheckDetailEvent: SharedFlow<Unit> =
        _showOnBoardingCheckDetailEvent.asSharedFlow()

    private val _navigateToRegisterLessonEvent = MutableSharedFlow<Unit>()
    val navigateRegisterLessonEvent: SharedFlow<Unit> =
        _navigateToRegisterLessonEvent.asSharedFlow()

    private val _navigateToNotificationListEvent = MutableSharedFlow<Unit>()
    val navigateToNotificationListEvent: SharedFlow<Unit> =
        _navigateToNotificationListEvent.asSharedFlow()

    private val _navigateToLessonDetailEvent = MutableSharedFlow<String>()
    val navigateToLessonDetailEvent: SharedFlow<String> =
        _navigateToLessonDetailEvent.asSharedFlow()

    private val _lessonList = MutableStateFlow<List<LessonUiModel>>(emptyList())
    val lessonList: StateFlow<List<LessonUiModel>> = _lessonList.asStateFlow()

    //TODO fetchLessonInfo 내에서 onBoarding 이 종료 되었는지 먼저 검사 하고
    // 종료가 되지 않았다면 빠꾸
    // 종료 되었다면 UseCase 를 호출하는 식으로 구현해야 UDF 를 위반하지 않는다
    // TODO stock market app 참고 해서 예외처리 코드 개선
    fun fetchLessonList() = viewModelScope.launch {
//        val isOnBoardingComplete = fetchOnBoardingStatusUseCase()
//        if (isOnBoardingComplete) {
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
//        } else {
//            _checkOnBoardingCompleteEvent.emit(false)
//        }
    }

    private fun setLessonList(result: List<LessonListResponse>) {
        _lessonList.update {
            if (result.isEmpty()) {
                listOf(
                    LessonUiModel.LessonHeader(HeaderAdapter.HeaderType.EMPTY, null),
                    LessonUiModel.EmptyLesson
                )
            } else {
                // groupBy 가 LessonUiMode 같은 group 이라고 판단 하지 못한다.
                result.groupBy {
                    it.lessonStatus
                }.values.map { lessonList ->
                    lessonList.map { lesson ->
                        when {
                            (lesson.isFinished || lesson.lessonStatus == "PAST") -> LessonUiModel.PastLesson(
                                lesson
                            )
                            (lesson.lessonStatus == "PLAN") -> LessonUiModel.PlanLesson(lesson)
                            else -> LessonUiModel.CurrentLesson(lesson)
                        }
                    }
                }.map { lessonUiModelList ->
                    lessonUiModelList.toMutableList().apply {
                        add(
                            0,
                            when (lessonUiModelList.first()) {
                                is LessonUiModel.CurrentLesson -> LessonUiModel.LessonHeader(
                                    HeaderAdapter.HeaderType.CURRENT, this.size
                                )
                                is LessonUiModel.PlanLesson -> LessonUiModel.LessonHeader(
                                    HeaderAdapter.HeaderType.PLAN, this.size
                                )
                                is LessonUiModel.PastLesson -> LessonUiModel.LessonHeader(
                                    HeaderAdapter.HeaderType.PAST, this.size
                                )
                                else -> return@apply
                            }
                        )
                    }
                }.flatten()
            }
        }
    }

    fun showOnBoardingCheckDetail() = viewModelScope.launch {
        _showOnBoardingCheckDetailEvent.emit(Unit)
    }

    fun checkOnBoardingComplete() = viewModelScope.launch {
        _checkOnBoardingCompleteEvent.emit(fetchOnBoardingStatusUseCase())
    }

    fun completeOnBoarding() = viewModelScope.launch {
        updateOnBoardingStatusUseCase()
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

    override fun retry() {
        fetchLessonList()
    }
}
