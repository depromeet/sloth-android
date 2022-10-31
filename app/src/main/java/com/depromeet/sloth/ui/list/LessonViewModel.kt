package com.depromeet.sloth.ui.list

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.repository.LessonRepository
import com.depromeet.sloth.data.network.lesson.list.LessonAllResponse
import com.depromeet.sloth.data.network.lesson.list.LessonFinishResponse
import com.depromeet.sloth.data.network.lesson.list.LessonTodayResponse
import com.depromeet.sloth.data.repository.MemberRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.base.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    memberRepository: MemberRepository,
) : BaseViewModel(memberRepository) {

    val todayLessonList: Flow<UiState<List<LessonTodayResponse>>> =
        lessonRepository.fetchTodayLessonList()

    val allLessonList: Flow<UiState<List<LessonAllResponse>>> =
        lessonRepository.fetchAllLessonList()

    fun finishLesson(lessonId: String): Flow<UiState<LessonFinishResponse>> =
        lessonRepository.finishLesson(lessonId)

    suspend fun updateLessonCount(count: Int, lessonId: Int) = withContext(viewModelScope.coroutineContext) {
        lessonRepository.updateLessonCount(count = count, lessonId = lessonId)
    }

    companion object {
        const val CURRENT = "CURRENT"
        const val PAST = "PAST"
    }
}