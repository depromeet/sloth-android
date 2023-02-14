package com.depromeet.sloth.presentation.screen.lessonlist

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonListResponse
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.usecase.lesson.FetchLessonListUseCase
import com.depromeet.sloth.domain.usecase.member.FetchOnBoardingStatusUseCase
import com.depromeet.sloth.domain.usecase.member.UpdateOnBoardingStatusUseCase
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

    private val _fetchLessonListSuccessEvent = MutableSharedFlow<Unit>()
    val fetchLessonListSuccessEvent: SharedFlow<Unit> = _fetchLessonListSuccessEvent.asSharedFlow()

//    private val _checkOnBoardingCompleteEvent = MutableSharedFlow<Boolean>(1)
//    val checkOnBoardingCompleteEvent: SharedFlow<Boolean> =
//        _checkOnBoardingCompleteEvent.asSharedFlow()

//    private val _showOnBoardingCheckDetailEvent = MutableSharedFlow<Unit>()
//    val showOnBoardingCheckDetailEvent: SharedFlow<Unit> =
//        _showOnBoardingCheckDetailEvent.asSharedFlow()

    private val _navigateToRegisterLessonEvent = MutableSharedFlow<Unit>()
    val navigateRegisterLessonEvent: SharedFlow<Unit> =
        _navigateToRegisterLessonEvent.asSharedFlow()

    private val _navigateToNotificationListEvent = MutableSharedFlow<Unit>()
    val navigateToNotificationListEvent: SharedFlow<Unit> =
        _navigateToNotificationListEvent.asSharedFlow()

    private val _navigateToLessonDetailEvent = MutableSharedFlow<LessonListResponse>()
    val navigateToLessonDetailEvent: SharedFlow<LessonListResponse> =
        _navigateToLessonDetailEvent.asSharedFlow()

    private val _emptyLessonList = MutableStateFlow(emptyList<LessonListResponse>())
    val emptyLessonList: StateFlow<List<LessonListResponse>> = _emptyLessonList.asStateFlow()

    private val _currentLessonList = MutableStateFlow(emptyList<LessonListResponse>())
    val currentLessonList: StateFlow<List<LessonListResponse>> = _currentLessonList.asStateFlow()

    private val _planLessonList = MutableStateFlow(emptyList<LessonListResponse>())
    val planLessonList: StateFlow<List<LessonListResponse>> = _planLessonList.asStateFlow()

    private val _pastLessonList = MutableStateFlow(emptyList<LessonListResponse>())
    val pastLessonList: StateFlow<List<LessonListResponse>> = _pastLessonList.asStateFlow()

    //TODO fetchLessonInfo 내에서 onBoarding 이 종료 되었는지 먼저 검사 하고
    // 종료가 되지 않았다면 빠꾸
    // 종료 되었다면 UseCase 를 호출하는 식으로 구현해야 UDF 를 위반하지 않는다
    // TODO stock market app 참고 해서 예외처리 코드 개선
    fun fetchLessonList() = viewModelScope.launch {
        val isOnBoardingComplete = fetchOnBoardingStatusUseCase()
        if (isOnBoardingComplete) {
            fetchLessonListUseCase()
                .onEach { result ->
                    setLoading(result is Result.Loading)
                }.collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> {
                            setInternetError(false)
                            // SuccessEvent 를 전달하지 말고 여기서 list를 세팅
                            setLessonList(result.data)
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
        } else {
//            _checkOnBoardingCompleteEvent.emit(false)
        }
    }

    //TODO race condition 이 발생할 위험은 없으나, update function 을 이용해야 할듯
    // lessonList가 비었을 경우에 대한 로직 처리
    private fun setLessonList(lessonList: List<LessonListResponse>) {
        initLessonList()
        if (lessonList.isNotEmpty()) {
            lessonList.forEach { lesson ->
                when (lesson.lessonStatus) {
                    "PAST" -> _pastLessonList.value =
                        pastLessonList.value.toMutableList().apply {
                            add(lesson)
                        }

                    "CURRENT" -> {
                        // 완강 목표일 보다 앞서 강의를 완강한 경우
                        if (lesson.isFinished) {
                            _pastLessonList.value =
                                pastLessonList.value.toMutableList().apply {
                                    add(lesson)
                                }
                        } else {
                            _currentLessonList.value =
                                currentLessonList.value.toMutableList().apply {
                                    add(lesson)
                                }
                        }
                    }
                    "PLAN" -> _planLessonList.value =
                        planLessonList.value.toMutableList().apply {
                            add(lesson)
                        }
                }
            }
        } else {
            _emptyLessonList.value = emptyLessonList.value.toMutableList().apply {
                add(LessonListResponse.EMPTY)
            }
        }
    }

    private fun initLessonList() {
        _emptyLessonList.value = emptyList()
        _currentLessonList.value = emptyList()
        _planLessonList.value = emptyList()
        _pastLessonList.value = emptyList()
    }

//    fun showOnBoardingCheckDetail() = viewModelScope.launch {
//        _showOnBoardingCheckDetailEvent.emit(Unit)
//    }
//
//    fun checkOnBoardingComplete() = viewModelScope.launch {
//        _checkOnBoardingCompleteEvent.emit(fetchOnBoardingStatusUseCase())
//    }
//
//    fun completeOnBoarding() = viewModelScope.launch {
//        updateOnBoardingStatusUseCase()
//    }

    fun navigateToRegisterLesson() = viewModelScope.launch {
        _navigateToRegisterLessonEvent.emit(Unit)
    }

    fun navigateToNotificationList() = viewModelScope.launch {
        _navigateToNotificationListEvent.emit(Unit)
    }

    fun navigateToLessonDetail(lesson: LessonListResponse) = viewModelScope.launch {
        _navigateToLessonDetailEvent.emit(lesson)
    }

    override fun retry() {
        fetchLessonList()
    }
}
