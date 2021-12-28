package com.depromeet.sloth.ui.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.lesson.LessonRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.withContext

class LessonDetailViewModel: BaseViewModel() {
    private val lessonRepository = LessonRepository()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchLessonDetail(accessToken: String, lessonId: String) =
        withContext(viewModelScope.coroutineContext) {
            lessonRepository.fetchLessonDetail(accessToken, lessonId)
        }

    suspend fun deleteLesson(accessToken: String, lessonId: String) =
        withContext(viewModelScope.coroutineContext) {
            lessonRepository.deleteLesson(accessToken, lessonId)
        }
}