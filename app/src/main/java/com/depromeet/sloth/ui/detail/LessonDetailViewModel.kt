package com.depromeet.sloth.ui.detail

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.lesson.LessonRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LessonDetailViewModel @Inject constructor(
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

    suspend fun fetchLessonCategoryList(accessToken: String) =
        withContext(viewModelScope.coroutineContext) {
            lessonRepository.fetchLessonCategoryList(accessToken)
        }

    suspend fun fetchLessonSiteList(accessToken: String) =
        withContext(viewModelScope.coroutineContext) {
            lessonRepository.fetchLessonSiteList(accessToken)
        }
}