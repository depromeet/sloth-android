package com.depromeet.sloth.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.lesson.*
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class LessonViewModel @ViewModelInject constructor (
    private val lessonRepository: LessonRepository
) : BaseViewModel() {
    suspend fun fetchTodayLessonList(
        accessToken: String,
        context: CoroutineContext = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT
    ): LessonState<List<LessonTodayResponse>> = viewModelScope.async(
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
    ): LessonState<List<LessonAllResponse>> = viewModelScope.async(
        context = context,
        start = start
    ) {
        lessonRepository.fetchAllLessonList(
            accessToken = accessToken
        )
    }.await()

    suspend fun updateLessonCount(
        accessToken: String,
        count: Int,
        lessonId: Int,
        context: CoroutineContext = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT
    ): LessonState<LessonUpdateCountResponse> = viewModelScope.async(
        context = context,
        start = start
    ) {
        lessonRepository.updateLessonCount(
            accessToken = accessToken,
            count = count,
            lessonId = lessonId
        )
    }.await()

    suspend fun removeAuthToken(pm: PreferenceManager) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                pm.removeAuthToken()
            }
        }
}