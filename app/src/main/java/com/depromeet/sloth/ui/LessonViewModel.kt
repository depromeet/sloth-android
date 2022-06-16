package com.depromeet.sloth.ui

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.lesson.*
import com.depromeet.sloth.data.network.lesson.list.*
import com.depromeet.sloth.data.network.lesson.LessonRepository
import com.depromeet.sloth.data.network.lesson.list.LessonAllResponse
import com.depromeet.sloth.data.network.lesson.list.LessonState
import com.depromeet.sloth.data.network.lesson.list.LessonTodayResponse
import com.depromeet.sloth.data.network.lesson.list.LessonUpdateCountResponse
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.base.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

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

    suspend fun updateLessonCount(
        count: Int,
        lessonId: Int,
        context: CoroutineContext = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT,
    ): LessonState<LessonUpdateCountResponse> = viewModelScope.async(
        context = context,
        start = start
    ) {
        lessonRepository.updateLessonCount(
            count = count,
            lessonId = lessonId
        )
    }.await()
}