package com.depromeet.sloth.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.data.model.response.lesson.LessonAllResponse
import com.depromeet.sloth.data.model.response.lesson.LessonFinishResponse
import com.depromeet.sloth.data.model.response.lesson.LessonTodayResponse
import com.depromeet.sloth.domain.use_case.lesson.FinishLessonUseCase
import com.depromeet.sloth.domain.use_case.lesson.GetAllLessonListUseCase
import com.depromeet.sloth.domain.use_case.lesson.GetTodayLessonListUseCase
import com.depromeet.sloth.domain.use_case.lesson.UpdateLessonCountUseCase
import com.depromeet.sloth.domain.use_case.member.RemoveAuthTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LessonListViewModel @Inject constructor(
    private val getLessonTodayListUseCase: GetTodayLessonListUseCase,
    getAllLessonListUseCase: GetAllLessonListUseCase,
    private val updateLessonCountUseCase: UpdateLessonCountUseCase,
    private val finishLessonUseCase: FinishLessonUseCase,
    private val removeAuthTokenUseCase: RemoveAuthTokenUseCase,
) : ViewModel() {

    private val _todayLessonListState = MutableSharedFlow<Result<List<LessonTodayResponse>>>()
    val todayLessonListState: SharedFlow<Result<List<LessonTodayResponse>>> = _todayLessonListState.asSharedFlow()

    val allLessonList: Flow<Result<List<LessonAllResponse>>> =
        getAllLessonListUseCase()

//    private val _updateLessonCountState = MutableSharedFlow<Result<LessonUpdateCountResponse>>()
//    val updateLessonCountState:SharedFlow<Result<LessonUpdateCountResponse>>
//            = _updateLessonCountState.asSharedFlow()

    private val _finishLessonState = MutableSharedFlow<Result<LessonFinishResponse>>()
    val finishLessonState: SharedFlow<Result<LessonFinishResponse>> = _finishLessonState

    private val _onRegisterLessonClick = MutableSharedFlow<Unit>()
    val onRegisterLessonClick: SharedFlow<Unit> = _onRegisterLessonClick.asSharedFlow()

    private val _onNavigateToNotificationListClick = MutableSharedFlow<Unit>()
    val onNavigateToNotificationListClick: SharedFlow<Unit> =
        _onNavigateToNotificationListClick.asSharedFlow()

    fun fetchTodayLessonList() = viewModelScope.launch {
        getLessonTodayListUseCase()
            .onEach {
                if (it is Result.Loading) _todayLessonListState.emit(Result.Loading)
                else _todayLessonListState.emit(Result.UnLoading)
            }.collect {
                _todayLessonListState.emit(it)
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
                if (it is Result.Loading) _finishLessonState.emit(Result.Loading)
                else _finishLessonState.emit(Result.UnLoading)
            }.collect {
                _finishLessonState.emit(it)
            }
    }

    fun registerLessonClick() = viewModelScope.launch {
        _onRegisterLessonClick.emit(Unit)
    }

    fun navigateToNotificationListClick() = viewModelScope.launch {
        _onNavigateToNotificationListClick.emit(Unit)
    }

    fun removeAuthToken() = viewModelScope.launch {
        removeAuthTokenUseCase()
    }
}
