package com.depromeet.sloth.presentation.screen.lessonlist

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonAllResponse
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.usecase.lesson.FetchAllLessonListUseCase
import com.depromeet.sloth.domain.usecase.member.FetchOnBoardingStatusUseCase
import com.depromeet.sloth.domain.usecase.member.UpdateOnBoardingStatusUseCase
import com.depromeet.sloth.presentation.screen.base.BaseViewModel
import com.depromeet.sloth.util.INTERNET_CONNECTION_ERROR
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonListViewModel @Inject constructor(
    private val fetchAllLessonListUseCase: FetchAllLessonListUseCase,
    private val fetchOnBoardingStatusUseCase: FetchOnBoardingStatusUseCase,
    private val updateOnBoardingStatusUseCase: UpdateOnBoardingStatusUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
) : BaseViewModel() {

    private val _fetchLessonListSuccessEvent = MutableSharedFlow<List<LessonAllResponse>>()
    val fetchLessonListSuccessEvent: SharedFlow<List<LessonAllResponse>> = _fetchLessonListSuccessEvent.asSharedFlow()

    private val _checkOnBoardingCompleteEvent = MutableSharedFlow<Boolean>(1)
    val checkOnBoardingCompleteEvent: SharedFlow<Boolean> = _checkOnBoardingCompleteEvent.asSharedFlow()

    private val _showOnBoardingCheckDetailEvent = MutableSharedFlow<Unit>()
    val showOnBoardingCheckDetailEvent: SharedFlow<Unit> =
        _showOnBoardingCheckDetailEvent.asSharedFlow()

    private val _navigateToRegisterLessonEvent = MutableSharedFlow<Unit>()
    val navigateRegisterLessonEvent: SharedFlow<Unit> = _navigateToRegisterLessonEvent.asSharedFlow()

    private val _navigateToNotificationListEvent = MutableSharedFlow<Unit>()
    val navigateToNotificationListEvent: SharedFlow<Unit> =
        _navigateToNotificationListEvent.asSharedFlow()

    private val _navigateToLessonDetailEvent = MutableSharedFlow<LessonAllResponse>()
    val navigateToLessonDetailEvent: SharedFlow<LessonAllResponse> = _navigateToLessonDetailEvent.asSharedFlow()

    init {
        checkOnBoardingComplete()
    }

    // TODO stock market app 참고 해서 함수 개선
    fun fetchAllLessonList() = viewModelScope.launch {
        fetchAllLessonListUseCase()
            .onEach { result ->
                setLoading( result is Result.Loading)
            }.collect { result ->
                when(result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        setInternetError(false)
                        _fetchLessonListSuccessEvent.emit(result.data)
                    }
                    is Result.Error -> {
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            setInternetError(true)
                        }
                        else if (result.statusCode == UNAUTHORIZED) {
                            navigateToExpireDialog()
                        }
                        else {
                            showToast(stringResourcesProvider.getString(R.string.lesson_fetch_fail))
                        }
                    }
                }
            }
    }

    fun showOnBoardingCheckDetail() = viewModelScope.launch {
        _showOnBoardingCheckDetailEvent.emit(Unit)
    }

    private fun checkOnBoardingComplete() = viewModelScope.launch {
        _checkOnBoardingCompleteEvent.emit(fetchOnBoardingStatusUseCase())
    }

    fun updateOnBoardingStatus() = viewModelScope.launch {
        updateOnBoardingStatusUseCase()
    }

    fun navigateToRegisterLesson() = viewModelScope.launch {
        _navigateToRegisterLessonEvent.emit(Unit)
    }

    fun navigateToNotificationList() = viewModelScope.launch {
        _navigateToNotificationListEvent.emit(Unit)
    }

    fun navigateToLessonDetail(lesson: LessonAllResponse) = viewModelScope.launch {
        _navigateToLessonDetailEvent.emit(lesson)
    }

    override fun retry() {
        fetchAllLessonList()
    }
}
