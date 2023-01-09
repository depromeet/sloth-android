package com.depromeet.sloth.ui.list

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonAllResponse
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.use_case.lesson.GetAllLessonListUseCase
import com.depromeet.sloth.domain.use_case.member.RemoveAuthTokenUseCase
import com.depromeet.sloth.ui.base.BaseViewModel
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
    private val getAllLessonListUseCase: GetAllLessonListUseCase,
    private val removeAuthTokenUseCase: RemoveAuthTokenUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
) : BaseViewModel() {

    private val _fetchLessonListSuccess = MutableSharedFlow<List<LessonAllResponse>>()
    val fetchLessonListSuccess: SharedFlow<List<LessonAllResponse>> = _fetchLessonListSuccess.asSharedFlow()

    private val _navigateToRegisterLessonEvent = MutableSharedFlow<Unit>()
    val navigateRegisterLessonEvent: SharedFlow<Unit> = _navigateToRegisterLessonEvent.asSharedFlow()

    private val _navigateToNotificationListEvent = MutableSharedFlow<Unit>()
    val navigateToNotificationListEvent: SharedFlow<Unit> =
        _navigateToNotificationListEvent.asSharedFlow()

    private val _navigateToLessonDetailEvent = MutableSharedFlow<LessonAllResponse>()
    val navigateToLessonDetailEvent: SharedFlow<LessonAllResponse> = _navigateToLessonDetailEvent.asSharedFlow()

    fun fetchAllLessonList() = viewModelScope.launch {
        getAllLessonListUseCase()
            .onEach { result ->
                showLoading( result is Result.Loading)
            }.collect { result ->
                when(result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        internetError(false)
                        _fetchLessonListSuccess.emit(result.data)
                    }
                    is Result.Error -> {
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            internetError(true)
                        }
                        else if (result.statusCode == UNAUTHORIZED) {
                            showForbiddenDialogEvent()
                        }
                        else {
                            showToastEvent(stringResourcesProvider.getString(R.string.lesson_fetch_fail))
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
