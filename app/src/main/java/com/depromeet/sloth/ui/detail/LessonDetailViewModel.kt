package com.depromeet.sloth.ui.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.lesson.LessonRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.withContext


class LessonDetailViewModel @ViewModelInject constructor(
    private val lessonRepository: LessonRepository
) : BaseViewModel() {
    suspend fun fetchLessonDetail(accessToken: String, lessonId: String) =
        withContext(viewModelScope.coroutineContext) {
            lessonRepository.fetchLessonDetail(accessToken, lessonId)
        }

    suspend fun deleteLesson(accessToken: String, lessonId: String) =
        withContext(viewModelScope.coroutineContext) {
            lessonRepository.deleteLesson(accessToken, lessonId)
        }
}