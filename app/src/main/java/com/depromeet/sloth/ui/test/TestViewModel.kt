package com.depromeet.sloth.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.health.HealthRepository
import com.depromeet.sloth.data.network.health.HealthResponse
import com.depromeet.sloth.data.network.health.HealthState
import com.depromeet.sloth.data.network.lesson.list.LessonState
import com.depromeet.sloth.data.network.lesson.list.LessonTodayResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class TestViewModel @Inject constructor(
    private val healthRepository: HealthRepository
) : ViewModel() {
    /**
     * Activity나 Fragment단에서 작업의 결과값을 리턴하여 State 분기를 편하게 처리할 수 있음
     *
     * @return Result<HealthResponse>
     */
//    suspend fun processHealthWork(
//        context: CoroutineContext = Dispatchers.IO,
//        start: CoroutineStart = CoroutineStart.DEFAULT,
//    ): HealthState<HealthResponse> {
//        return viewModelScope.async(context = context, start = start) {
//            healthRepository.getHealth()
//        }.await()
//    }
//
//    suspend fun fetchTodayLessonList(
//        context: CoroutineContext = Dispatchers.IO,
//        start: CoroutineStart = CoroutineStart.DEFAULT,
//    ): LessonState<List<LessonTodayResponse>> = viewModelScope.async(
//        context = context,
//        start = start
//    ) {
//        healthRepository.fetchTodayLessonList()
//    }.await()
}