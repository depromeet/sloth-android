package com.depromeet.sloth.ui.update

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.update.UpdateLessonRepository
import com.depromeet.sloth.data.network.update.UpdateLessonRequest
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.*

class UpdateLessonViewModel : BaseViewModel() {
    private val updateLessonRepository = UpdateLessonRepository()

    suspend fun updateLessonInfo(
        accessToken: String,
        lessonId: String,
        updateLessonRequest: UpdateLessonRequest
    ) = viewModelScope.async {
        updateLessonRepository.updateLessonInfo(accessToken, lessonId, updateLessonRequest)
    }.await()
}