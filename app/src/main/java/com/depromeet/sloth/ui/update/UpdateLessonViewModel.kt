package com.depromeet.sloth.ui.update

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.UpdateLessonModel
import com.depromeet.sloth.data.network.update.UpdateLessonRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.*

class UpdateLessonViewModel: BaseViewModel() {
    private val updateLessonRepository = UpdateLessonRepository()

    suspend fun updateLessonInfo(accessToken: String, lessonId: String, updateLessonModel: UpdateLessonModel) = viewModelScope.async {
        updateLessonRepository.updateLessonInfo(accessToken, lessonId, updateLessonModel)
    }.await()
}