package com.depromeet.sloth.ui.list

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.common.Result
import com.depromeet.sloth.data.model.response.lesson.LessonAllResponse
import com.depromeet.sloth.data.model.response.lesson.LessonFinishResponse
import com.depromeet.sloth.data.model.response.lesson.LessonTodayResponse
import com.depromeet.sloth.data.repository.LessonRepository
import com.depromeet.sloth.data.repository.MemberRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LessonListViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    memberRepository: MemberRepository,
) : BaseViewModel(memberRepository) {

    val todayLessonList: Flow<Result<List<LessonTodayResponse>>> =
        lessonRepository.fetchTodayLessonList()

    val allLessonList: Flow<Result<List<LessonAllResponse>>> =
        lessonRepository.fetchAllLessonList()

//    private val _updateLessonCountState = MutableSharedFlow<Result<LessonUpdateCountResponse>>()
//    val updateLessonCountState:SharedFlow<Result<LessonUpdateCountResponse>>
//            = _updateLessonCountState.asSharedFlow()

    private val _onRegisterLessonClick = MutableSharedFlow<Unit>()
    val onRegisterLessonClick: SharedFlow<Unit> = _onRegisterLessonClick.asSharedFlow()

    private val _onNavigateToNotificationListClick = MutableSharedFlow<Unit>()
    val onNavigateToNotificationListClick: SharedFlow<Unit> =
        _onNavigateToNotificationListClick.asSharedFlow()

    fun finishLesson(lessonId: String): Flow<Result<LessonFinishResponse>> =
        lessonRepository.finishLesson(lessonId)

    suspend fun updateLessonCount(count: Int, lessonId: Int) =
        withContext(viewModelScope.coroutineContext) {
            lessonRepository.updateLessonCount(count = count, lessonId = lessonId)
        }

//    fun updateLessonCount(count: Int, lessonId: Int) = viewModelScope.launch {
//        lessonRepository.updateLessonCount(count, lessonId)
//            .onEach {
//                if (it is Result.Loading) _updateLessonCountState.emit(Result.Loading)
//                else _updateLessonCountState.emit(Result.UnLoading)
//            }.collect {
//                _updateLessonCountState.emit(it)
//            }
//    }

    fun registerLessonClick() = viewModelScope.launch {
        _onRegisterLessonClick.emit(Unit)
    }

    fun navigateToNotificationListClick() = viewModelScope.launch {
        _onNavigateToNotificationListClick.emit(Unit)
    }
}