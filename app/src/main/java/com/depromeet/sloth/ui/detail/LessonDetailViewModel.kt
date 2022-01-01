package com.depromeet.sloth.ui.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.lesson.LessonRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LessonDetailViewModel(
    preferenceManager: PreferenceManager
) : BaseViewModel() {
    private val lessonRepository = LessonRepository(preferenceManager)

    suspend fun fetchLessonDetail(accessToken: String, lessonId: String) =
        withContext(viewModelScope.coroutineContext) {
            lessonRepository.fetchLessonDetail(accessToken, lessonId)
        }

    suspend fun deleteLesson(accessToken: String, lessonId: String) =
        withContext(viewModelScope.coroutineContext) {
            lessonRepository.deleteLesson(accessToken, lessonId)
        }

    suspend fun removeAuthToken(pm: PreferenceManager) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            pm.removeAuthToken()
        }
    }
}