package com.depromeet.sloth.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.LessonDetail
import com.depromeet.sloth.data.network.lesson.*
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteState
import com.depromeet.sloth.data.network.lesson.detail.LessonDetailResponse
import com.depromeet.sloth.data.network.lesson.detail.LessonDetailState
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LessonDetailViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    private val memberRepository: MemberRepository,
) : BaseViewModel() {

    private val _lessonDetailState = MutableLiveData<LessonDetailState<LessonDetailResponse>>()
    val lessonDetailState: LiveData<LessonDetailState<LessonDetailResponse>> = _lessonDetailState


    private val _lessonDeleteState = MutableLiveData<LessonDeleteState<LessonDeleteResponse>>()
    val lessonDeleteState: LiveData<LessonDeleteState<LessonDeleteResponse>> = _lessonDeleteState

    private val _lessonDetail = MutableLiveData<LessonDetail>()
    val lessonDetail: LiveData<LessonDetail> = _lessonDetail

    private val _lessonId = MutableLiveData<String>()
    val lessonId: LiveData<String> = _lessonId

    fun fetchLessonDetail(lessonId: String) = viewModelScope.launch {
        _lessonDetailState.value = LessonDetailState.Loading
        val lessonDetailResponse = lessonRepository.fetchLessonDetail(lessonId)
        _lessonDetailState.value = lessonDetailResponse
    }

    fun deleteLesson(lessonId: String) = viewModelScope.launch {
        _lessonDeleteState.value = LessonDeleteState.Loading
        val lessonDeleteResponse = lessonRepository.deleteLesson(lessonId)
        _lessonDeleteState.value = lessonDeleteResponse
    }

    fun setLessonInfo(lessonDetailResponse: LessonDetailResponse) = with(lessonDetailResponse) {
        Log.d("LessonDetailViewModel", "setLessonInfo: 호출")
        _lessonDetail.value = LessonDetail(
            alertDays = alertDays,
            categoryName = categoryName,
            currentProgressRate = currentProgressRate.toFloat(),
            endDate = endDate,
            goalProgressRate = goalProgressRate.toFloat(),
            isFinished = isFinished,
            lessonId = lessonId,
            lessonName = lessonName,
            message = message,
            presentNumber = presentNumber,
            price = price,
            remainDay = remainDay,
            siteName = siteName,
            startDate = startDate,
            totalNumber = totalNumber,
            wastePrice = wastePrice
        )
        Log.d("LessonDetailViewModel", "setLessonInfo: ${_lessonDetail.value}")
    }

    fun removeAuthToken() = viewModelScope.launch {
        memberRepository.removeAuthToken()
    }
}