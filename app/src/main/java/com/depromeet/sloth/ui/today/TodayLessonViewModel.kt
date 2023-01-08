package com.depromeet.sloth.ui.today

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.response.lesson.LessonTodayResponse
import com.depromeet.sloth.domain.use_case.lesson.FinishLessonUseCase
import com.depromeet.sloth.domain.use_case.lesson.GetTodayLessonListUseCase
import com.depromeet.sloth.domain.use_case.lesson.UpdateLessonCountUseCase
import com.depromeet.sloth.domain.use_case.login.CheckLoggedInUseCase
import com.depromeet.sloth.domain.use_case.member.RemoveAuthTokenUseCase
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.util.Result
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
    private val getLessonTodayListUseCase: GetTodayLessonListUseCase,
    private val updateLessonCountUseCase: UpdateLessonCountUseCase,
    private val finishLessonUseCase: FinishLessonUseCase,
    private val removeAuthTokenUseCase: RemoveAuthTokenUseCase,
) : BaseViewModel() {

    private val _autoLoginEvent = MutableSharedFlow<Boolean>(replay = 1)
    val autoLoginEvent: SharedFlow<Boolean> = _autoLoginEvent.asSharedFlow()

    init {
        checkLoggedIn()
    }

    private val _fetchLessonListSuccess = MutableSharedFlow<List<LessonTodayResponse>>()
    val fetchLessonListSuccess: SharedFlow<List<LessonTodayResponse>> = _fetchLessonListSuccess.asSharedFlow()

    private val _fetchLessonListFail = MutableSharedFlow<Int>()
    val fetchLessonListFail: SharedFlow<Int> = _fetchLessonListFail.asSharedFlow()

    private val _finishLessonSuccess = MutableSharedFlow<Unit>()
    val finishLessonSuccess: SharedFlow<Unit> = _finishLessonSuccess.asSharedFlow()

    private val _finishLessonFail = MutableSharedFlow<Int>()
    val finishLessonFail: SharedFlow<Int> = _finishLessonFail.asSharedFlow()

    private val _navigateToRegisterLessonEvent = MutableSharedFlow<Unit>()
    val navigateRegisterLessonEvent: SharedFlow<Unit> = _navigateToRegisterLessonEvent.asSharedFlow()

    private val _navigateToNotificationListEvent = MutableSharedFlow<Unit>()
    val navigateToNotificationListEvent: SharedFlow<Unit> =
        _navigateToNotificationListEvent.asSharedFlow()
    
    private fun checkLoggedIn() = viewModelScope.launch {
        _autoLoginEvent.emit(checkLoggedInUseCase())
    }

    fun fetchTodayLessonList() = viewModelScope.launch {
        getLessonTodayListUseCase()
            .onEach { result ->
                setLoading(result is Result.Loading)
            }.collect {result ->
                when(result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> _fetchLessonListSuccess.emit(result.data)
                    is Result.Error -> result.statusCode?.let { _fetchLessonListFail.emit(it) }
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
                setLoading(result is Result.Loading)
            }.collect { result ->
                when(result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> _finishLessonSuccess.emit(Unit)
                    is Result.Error -> result.statusCode?.let { _finishLessonFail.emit(it)}
                }
            }
    }

    fun navigateToRegisterLesson() = viewModelScope.launch {
        _navigateToRegisterLessonEvent.emit(Unit)
    }

    fun navigateToNotificationList() = viewModelScope.launch {
        _navigateToNotificationListEvent.emit(Unit)
    }

    fun removeAuthToken() = viewModelScope.launch {
        removeAuthTokenUseCase()
    }

    override fun retry() {
        fetchTodayLessonList()
    }
}
