package com.depromeet.sloth.presentation.today

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonTodayResponse
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.use_case.lesson.FinishLessonUseCase
import com.depromeet.sloth.domain.use_case.lesson.FetchTodayLessonListUseCase
import com.depromeet.sloth.domain.use_case.lesson.UpdateLessonCountUseCase
import com.depromeet.sloth.domain.use_case.login.CheckLoggedInUseCase
import com.depromeet.sloth.domain.use_case.member.DeleteAuthTokenUseCase
import com.depromeet.sloth.presentation.base.BaseViewModel
import com.depromeet.sloth.util.INTERNET_CONNECTION_ERROR
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TodayLessonViewModel @Inject constructor(
    private val checkLoggedInUseCase: CheckLoggedInUseCase,
    private val getLessonTodayListUseCase: FetchTodayLessonListUseCase,
    private val updateLessonCountUseCase: UpdateLessonCountUseCase,
    private val finishLessonUseCase: FinishLessonUseCase,
    private val deleteAuthTokenUseCase: DeleteAuthTokenUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
) : BaseViewModel() {

    private val _autoLoginEvent = MutableSharedFlow<Boolean>(replay = 1)
    val autoLoginEvent: SharedFlow<Boolean> = _autoLoginEvent.asSharedFlow()

    init {
        checkLoggedIn()
    }

    private val _fetchLessonListSuccess = MutableSharedFlow<List<LessonTodayResponse>>()
    val fetchLessonListSuccess: SharedFlow<List<LessonTodayResponse>> =
        _fetchLessonListSuccess.asSharedFlow()

    private val _navigateToRegisterLessonEvent = MutableSharedFlow<Unit>()
    val navigateRegisterLessonEvent: SharedFlow<Unit> =
        _navigateToRegisterLessonEvent.asSharedFlow()

    private val _navigateToNotificationListEvent = MutableSharedFlow<Unit>()
    val navigateToNotificationListEvent: SharedFlow<Unit> =
        _navigateToNotificationListEvent.asSharedFlow()

    private fun checkLoggedIn() = viewModelScope.launch {
        _autoLoginEvent.emit(checkLoggedInUseCase())
    }

    fun fetchTodayLessonList() = viewModelScope.launch {
        getLessonTodayListUseCase()
            .onEach { result ->
                showLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        internetError(false)
                        _fetchLessonListSuccess.emit(result.data)
                    }
                    is Result.Error -> {
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            internetError(true)
                        } else if (result.statusCode == UNAUTHORIZED) {
                            showForbiddenDialogEvent()
                        } else {
                            showToastEvent(stringResourcesProvider.getString(R.string.lesson_fetch_fail))
                        }
                    }
                }
            }
    }

    //TODO 다른 함수들과 같은 형태로 변경
    suspend fun updateLessonCount(count: Int, lessonId: Int) =
        withContext(viewModelScope.coroutineContext) {
            updateLessonCountUseCase(count = count, lessonId = lessonId)
        }

    fun finishLesson(lessonId: String) = viewModelScope.launch {
        finishLessonUseCase(lessonId)
            .onEach { result ->
                showLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        showToastEvent(stringResourcesProvider.getString(R.string.lesson_finish_complete))
                        fetchTodayLessonList()
                    }
                    is Result.Error -> {
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            showToastEvent(stringResourcesProvider.getString(R.string.lesson_finish_fail_by_internet_error))
                        }
                        else if (result.statusCode == UNAUTHORIZED) {
                            showForbiddenDialogEvent()
                        }
                        else {
                            showToastEvent(stringResourcesProvider.getString(R.string.lesson_finish_fail))
                        }
                    }
                }
            }
    }

    fun navigateToRegisterLesson() = viewModelScope.launch {
        _navigateToRegisterLessonEvent.emit(Unit)
    }

    fun navigateToNotificationList() = viewModelScope.launch {
        _navigateToNotificationListEvent.emit(Unit)
    }

    fun deleteAuthToken() = viewModelScope.launch {
        deleteAuthTokenUseCase()
    }

    override fun retry() {
        fetchTodayLessonList()
    }
}
