package com.depromeet.sloth.ui.list

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.repository.LessonRepository
import com.depromeet.sloth.data.network.lesson.list.LessonAllResponse
import com.depromeet.sloth.data.network.lesson.list.LessonFinishResponse
import com.depromeet.sloth.data.network.lesson.list.LessonTodayResponse
import com.depromeet.sloth.data.repository.MemberRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.base.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    memberRepository: MemberRepository,
) : BaseViewModel(memberRepository) {

    val todayLessonList: Flow<UIState<List<LessonTodayResponse>>> =
        lessonRepository.fetchTodayLessonList()

//        suspend fun fetchTodayLessonList(
//        context: CoroutineContext = Dispatchers.IO,
//        start: CoroutineStart = CoroutineStart.DEFAULT,
//    ): LessonState<List<LessonTodayResponse>> = viewModelScope.async(
//        context = context,
//        start = start
//    ) {
//        lessonRepository.fetchTodayLessonList()
//    }.await()

    val allLessonList: Flow<UIState<List<LessonAllResponse>>> =
        lessonRepository.fetchAllLessonList()
//
//    suspend fun fetchAllLessonList(
//        context: CoroutineContext = Dispatchers.IO,
//        start: CoroutineStart = CoroutineStart.DEFAULT,
//    ): LessonState<List<LessonAllResponse>> = viewModelScope.async(
//        context = context,
//        start = start
//    ) {
//        lessonRepository.fetchAllLessonList()
//    }.await()

    fun finishLesson(lessonId: String): Flow<UIState<LessonFinishResponse>> =
        lessonRepository.finishLesson(lessonId)

    suspend fun updateLessonCount(count: Int, lessonId: Int) = withContext(viewModelScope.coroutineContext) {
        lessonRepository.updateLessonCount(count = count, lessonId = lessonId)
    }

    companion object {
        const val CURRENT = "CURRENT"
        const val PAST = "PAST"
    }
}