package com.depromeet.sloth.ui

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.lesson.*
import com.depromeet.sloth.data.network.lesson.list.LessonAllResponse
import com.depromeet.sloth.data.network.lesson.list.LessonState
import com.depromeet.sloth.data.network.lesson.list.LessonTodayResponse
import com.depromeet.sloth.data.network.lesson.list.LessonUpdateCountResponse
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    memberRepository: MemberRepository,
) : BaseViewModel(memberRepository) {
    suspend fun fetchTodayLessonList(
        context: CoroutineContext = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT,
    ): LessonState<List<LessonTodayResponse>> = viewModelScope.async(
        context = context,
        start = start
    ) {
        lessonRepository.fetchTodayLessonList()
    }.await()

    suspend fun fetchAllLessonList(
        context: CoroutineContext = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT,
    ): LessonState<List<LessonAllResponse>> = viewModelScope.async(
        context = context,
        start = start
    ) {
        lessonRepository.fetchAllLessonList()
    }.await()

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