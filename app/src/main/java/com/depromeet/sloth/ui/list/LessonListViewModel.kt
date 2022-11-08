package com.depromeet.sloth.ui.list

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.repository.LessonRepository
import com.depromeet.sloth.data.network.lesson.list.LessonAllResponse
import com.depromeet.sloth.data.network.lesson.list.LessonFinishResponse
import com.depromeet.sloth.data.network.lesson.list.LessonTodayResponse
import com.depromeet.sloth.data.repository.MemberRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.common.UiState
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

    val todayLessonList: Flow<UiState<List<LessonTodayResponse>>> =
        lessonRepository.fetchTodayLessonList()

    val allLessonList: Flow<UiState<List<LessonAllResponse>>> =
        lessonRepository.fetchAllLessonList()

    private val _onRegisterLessonClick = MutableSharedFlow<Unit>()
    val onRegisterLessonClick: SharedFlow<Unit> = _onRegisterLessonClick.asSharedFlow()

    private val _onNavigateToNotificationListClick = MutableSharedFlow<Unit>()
    val onNavigateToNotificationListClick: SharedFlow<Unit> =
        _onNavigateToNotificationListClick.asSharedFlow()

    fun finishLesson(lessonId: String): Flow<UiState<LessonFinishResponse>> =
        lessonRepository.finishLesson(lessonId)

    suspend fun updateLessonCount(count: Int, lessonId: Int) =
        withContext(viewModelScope.coroutineContext) {
            lessonRepository.updateLessonCount(count = count, lessonId = lessonId)
        }

    fun registerLessonClick() = viewModelScope.launch {
        _onRegisterLessonClick.emit(Unit)
    }

    fun navigateToNotificationListClick() = viewModelScope.launch {
        _onNavigateToNotificationListClick.emit(Unit)
    }
}