package com.depromeet.sloth.ui.today

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.response.lesson.LessonAllResponse
import com.depromeet.sloth.data.model.response.lesson.LessonFinishResponse
import com.depromeet.sloth.data.model.response.lesson.LessonTodayResponse
import com.depromeet.sloth.data.model.response.lesson.LessonUpdateCountResponse
import com.depromeet.sloth.domain.use_case.lesson.FinishLessonUseCase
import com.depromeet.sloth.domain.use_case.lesson.GetTodayLessonListUseCase
import com.depromeet.sloth.domain.use_case.lesson.UpdateLessonCountUseCase
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
    private val getLessonTodayListUseCase: GetTodayLessonListUseCase,
    private val updateLessonCountUseCase: UpdateLessonCountUseCase,
    private val finishLessonUseCase: FinishLessonUseCase,
    private val removeAuthTokenUseCase: RemoveAuthTokenUseCase,
) : BaseViewModel() {

    private val _fetchTodayLessonListEvent = MutableSharedFlow<Result<List<LessonTodayResponse>>>()
    val fetchTodayLessonListEvent: SharedFlow<Result<List<LessonTodayResponse>>> = _fetchTodayLessonListEvent.asSharedFlow()

    private val _updateLessonCountEvent = MutableSharedFlow<Result<LessonUpdateCountResponse>>()
    val updateLessonCountEvent: SharedFlow<Result<LessonUpdateCountResponse>>
            = _updateLessonCountEvent.asSharedFlow()

    private val _finishLessonEvent = MutableSharedFlow<Result<LessonFinishResponse>>()
    val finishLessonEvent: SharedFlow<Result<LessonFinishResponse>> = _finishLessonEvent

    private val _navigateToRegisterLessonEvent = MutableSharedFlow<Unit>()
    val navigateRegisterLessonEvent: SharedFlow<Unit> = _navigateToRegisterLessonEvent.asSharedFlow()

    private val _navigateToNotificationListEvent = MutableSharedFlow<Unit>()
    val navigateToNotificationListEvent: SharedFlow<Unit> =
        _navigateToNotificationListEvent.asSharedFlow()

    private val _navigateToLessonDetailEvent = MutableSharedFlow<LessonAllResponse>()
    val navigateToLessonDetailEvent: SharedFlow<LessonAllResponse> = _navigateToLessonDetailEvent.asSharedFlow()

    fun fetchTodayLessonList() = viewModelScope.launch {
        getLessonTodayListUseCase()
            .onEach {
                if (it is Result.Loading) _fetchTodayLessonListEvent.emit(Result.Loading)
                else _fetchTodayLessonListEvent.emit(Result.UnLoading)
            }.collect {
                _fetchTodayLessonListEvent.emit(it)
            }
    }

    suspend fun updateLessonCount(count: Int, lessonId: Int) =
        withContext(viewModelScope.coroutineContext) {
            updateLessonCountUseCase(count = count, lessonId = lessonId)
        }

//    fun updateLessonCount(count: Int, lessonId: Int) = viewModelScope.launch {
//            updateLessonCountUseCase(count, lessonId)
//            .onEach {
//                if (it is Result.Loading) _updateLessonCountState.emit(Result.Loading)
//                else _updateLessonCountState.emit(Result.UnLoading)
//            }.collect {
//                _updateLessonCountState.emit(it)
//            }
//    }

    fun finishLesson(lessonId: String) = viewModelScope.launch {
        finishLessonUseCase(lessonId)
            .onEach {
                if (it is Result.Loading) _finishLessonEvent.emit(Result.Loading)
                else _finishLessonEvent.emit(Result.UnLoading)
            }.collect {
                _finishLessonEvent.emit(it)
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
