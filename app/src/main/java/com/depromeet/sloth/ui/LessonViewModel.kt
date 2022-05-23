package com.depromeet.sloth.ui

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.lesson.*
import com.depromeet.sloth.data.network.lesson.list.LessonAllResponse
import com.depromeet.sloth.data.network.lesson.list.LessonState
import com.depromeet.sloth.data.network.lesson.list.LessonTodayResponse
import com.depromeet.sloth.data.network.lesson.list.LessonUpdateCountResponse
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    memberRepository: MemberRepository,
) : BaseViewModel(memberRepository) {
    suspend fun fetchTodayLessonList(
        context: CoroutineContext = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT,
    ): LessonState<List<LessonTodayResponse>> = viewModelScope.async(
        context = context,
        start = start
    ) {
        lessonRepository.fetchTodayLessonList()
    }.await()

    suspend fun fetchAllLessonList(
        context: CoroutineContext = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT,
    ): LessonState<List<LessonAllResponse>> = viewModelScope.async(
        context = context,
        start = start
    ) {
        lessonRepository.fetchAllLessonList()
    }.await()

    suspend fun updateLessonCount(
        count: Int,
        lessonId: Int,
        context: CoroutineContext = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT,
    ): LessonState<LessonUpdateCountResponse> = viewModelScope.async(
        context = context,
        start = start
    ) {
        lessonRepository.updateLessonCount(
            count = count,
            lessonId = lessonId
        )
    }.await()

    private val _todayLessonList = MutableStateFlow<LessonState<List<LessonTodayResponse>>>(LessonState.Loading)
    val todayLessonList: StateFlow<LessonState<List<LessonTodayResponse>>> = _todayLessonList

    fun fetchTodayLessonListTest() {
        viewModelScope.launch {
            lessonRepository.fetchTodayLessonListTest().collect { lessonState ->
                _todayLessonList.value = when(lessonState) {
                    is LessonState.Loading -> LessonState.Loading
                    is LessonState.Success<List<LessonTodayResponse>> -> LessonState.Success(lessonState.data)
                    is LessonState.Unauthorized -> LessonState.Unauthorized(lessonState.exception)
                    is LessonState.Error -> LessonState.Error(lessonState.exception)
                }
            }
        }
    }

    private val _allLessonList = MutableStateFlow<LessonState<List<LessonAllResponse>>>(LessonState.Loading)
    val allLessonList: StateFlow<LessonState<List<LessonAllResponse>>> = _allLessonList

    fun fetchAllLessonListTest() {
        viewModelScope.launch {
            lessonRepository.fetchAllLessonListTest().collect { lessonState ->
                _allLessonList.value = when(lessonState) {
                    is LessonState.Loading -> LessonState.Loading
                    is LessonState.Success -> LessonState.Success(lessonState.data)
                    is LessonState.Unauthorized -> LessonState.Unauthorized(lessonState.exception)
                    is LessonState.Error -> LessonState.Error(lessonState.exception)
                }
            }
        }
    }

    init {
        fetchAllLessonListTest()
        fetchTodayLessonListTest()
    }
}