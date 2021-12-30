package com.depromeet.sloth.ui.update

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.lesson.LessonRepository
import com.depromeet.sloth.data.network.lesson.LessonUpdateInfoRequest
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.*

class UpdateLessonViewModel : BaseViewModel() {
    private val lessonRepository = LessonRepository()

    suspend fun updateLesson(
        accessToken: String,
        lessonId: String,
        updateLessonRequest: LessonUpdateInfoRequest
    ) = viewModelScope.async {
        lessonRepository.updateLesson(accessToken, lessonId, updateLessonRequest)
    }.await()

    suspend fun fetchLessonCategoryList(accessToken: String) = viewModelScope.async {
        lessonRepository.fetchLessonCategoryList(accessToken)
    }.await()

    suspend fun fetchLessonSiteList(accessToken: String) = viewModelScope.async {
        lessonRepository.fetchLessonSiteList(accessToken)
    }.await()

    suspend fun removeAuthToken(pm: PreferenceManager) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                pm.removeAuthToken()
            }
        }
}