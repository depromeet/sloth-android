package com.depromeet.sloth.ui.home

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.home.*
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlin.coroutines.CoroutineContext

class LessonViewModel : BaseViewModel() {
    private val lessonRepository = LessonRepository()

    suspend fun fetchTodayLessonList(
        accessToken: String,
        context: CoroutineContext = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT
    ): LessonState<List<TodayLessonResponse>> = viewModelScope.async(
        context = context,
        start = start
    ) {
        lessonRepository.fetchTodayLessonList(
            accessToken = accessToken
        )
    }.await()

    suspend fun fetchAllLessonList(
        accessToken: String,
        context: CoroutineContext = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT
    ): LessonState<List<AllLessonResponse>> = viewModelScope.async(
        context = context,
        start = start
    ) {
        lessonRepository.fetchAllLessonList(
            accessToken = accessToken
        )
    }.await()
}