package com.depromeet.sloth.ui.list

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.response.lesson.LessonAllResponse
import com.depromeet.sloth.domain.use_case.lesson.GetAllLessonListUseCase
import com.depromeet.sloth.domain.use_case.member.RemoveAuthTokenUseCase
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonListViewModel @Inject constructor(
    private val getAllLessonListUseCase: GetAllLessonListUseCase,
    private val removeAuthTokenUseCase: RemoveAuthTokenUseCase,
) : BaseViewModel() {

//    val fetchAllLessonListEvent: Flow<Result<List<LessonAllResponse>>> =
//        getAllLessonListUseCase()

    private val _fetchAllLessonListEvent = MutableSharedFlow<Result<List<LessonAllResponse>>>()
    val fetchAllLessonListEvent: SharedFlow<Result<List<LessonAllResponse>>> = _fetchAllLessonListEvent.asSharedFlow()

    private val _navigateToRegisterLessonEvent = MutableSharedFlow<Unit>()
    val navigateRegisterLessonEvent: SharedFlow<Unit> = _navigateToRegisterLessonEvent.asSharedFlow()

    private val _navigateToNotificationListEvent = MutableSharedFlow<Unit>()
    val navigateToNotificationListEvent: SharedFlow<Unit> =
        _navigateToNotificationListEvent.asSharedFlow()

    private val _navigateToLessonDetailEvent = MutableSharedFlow<LessonAllResponse>()
    val navigateToLessonDetailEvent: SharedFlow<LessonAllResponse> = _navigateToLessonDetailEvent.asSharedFlow()

    fun fetchAllLessonList() = viewModelScope.launch {
        getAllLessonListUseCase()
            .onEach {
                if (it is Result.Loading) _fetchAllLessonListEvent.emit(Result.Loading)
                else _fetchAllLessonListEvent.emit(Result.UnLoading)
            }.collect {
                _fetchAllLessonListEvent.emit(it)
            }
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

    fun removeAuthToken() = viewModelScope.launch {
        removeAuthTokenUseCase()
    }

    override fun retry() {
        fetchAllLessonList()
    }
}
