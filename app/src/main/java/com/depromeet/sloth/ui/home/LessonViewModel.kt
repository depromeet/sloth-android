package com.depromeet.sloth.ui.home

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.home.LessonListState
import com.depromeet.sloth.data.network.home.LessonRepository
import com.depromeet.sloth.data.network.home.LessonResponse
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlin.coroutines.CoroutineContext

class LessonViewModel : BaseViewModel() {
    private val lessonRepository = LessonRepository()

    suspend fun fetchLessonList(
        accessToken: String,
        context: CoroutineContext = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT
    ): LessonListState<List<LessonResponse>> = viewModelScope.async(
        context = context,
        start = start
    ) {
        lessonRepository.fetchLessonList(
            accessToken = accessToken
        )
    }.await()
}