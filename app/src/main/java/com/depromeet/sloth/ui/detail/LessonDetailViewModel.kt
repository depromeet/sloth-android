package com.depromeet.sloth.ui.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.detail.LessonDetailRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.async

class LessonDetailViewModel: BaseViewModel() {
    private val lessonDetailRepository = LessonDetailRepository()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchLessonDetailInfo(accessToken: String, lessonId: String) = viewModelScope.async {
        lessonDetailRepository.fetchLessonDetailInfo(accessToken, lessonId)
    }.await()
}