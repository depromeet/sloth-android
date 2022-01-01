package com.depromeet.sloth.ui.update

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.lesson.LessonRepository
import com.depromeet.sloth.data.network.lesson.LessonUpdateInfoRequest
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateLessonViewModel(
    preferenceManager: PreferenceManager
) : BaseViewModel() {
    private val lessonRepository = LessonRepository(preferenceManager)

    suspend fun updateLesson(
        accessToken: String,
        lessonId: String,
        updateLessonRequest: LessonUpdateInfoRequest
    ) = withContext(viewModelScope.coroutineContext) {
        lessonRepository.updateLesson(accessToken, lessonId, updateLessonRequest)
    }

    suspend fun fetchLessonCategoryList(accessToken: String) =
        withContext(viewModelScope.coroutineContext) {
            lessonRepository.fetchLessonCategoryList(accessToken)
        }

    suspend fun fetchLessonSiteList(accessToken: String) =
        withContext(viewModelScope.coroutineContext) {
            lessonRepository.fetchLessonSiteList(accessToken)
        }

    suspend fun removeAuthToken(pm: PreferenceManager) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            pm.removeAuthToken()
        }
    }
}