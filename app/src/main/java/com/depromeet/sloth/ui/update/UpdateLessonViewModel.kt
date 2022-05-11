package com.depromeet.sloth.ui.update

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.LessonDetail
import com.depromeet.sloth.data.network.lesson.LessonRepository
import com.depromeet.sloth.data.network.lesson.category.LessonCategoryResponse
import com.depromeet.sloth.data.network.lesson.list.LessonState
import com.depromeet.sloth.data.network.lesson.site.LessonSiteResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateRequest
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateState
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateLessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    memberRepository: MemberRepository,
) : BaseViewModel(memberRepository) {

    private val _lessonUpdateState = MutableLiveData<LessonUpdateState<LessonUpdateResponse>>()
    val lessonUpdateState: LiveData<LessonUpdateState<LessonUpdateResponse>> = _lessonUpdateState

    private val _lessonCategoryListState =
        MutableLiveData<LessonState<List<LessonCategoryResponse>>>()
    val lessonCategoryListState: LiveData<LessonState<List<LessonCategoryResponse>>> =
        _lessonCategoryListState

    private val _lessonSiteListState = MutableLiveData<LessonState<List<LessonSiteResponse>>>()
    val lessonSiteListState: LiveData<LessonState<List<LessonSiteResponse>>> = _lessonSiteListState

    private val _lessonDetail = MutableLiveData<LessonDetail>()
    val lessonDetail: LiveData<LessonDetail> = _lessonDetail

    private val _lessonCategoryList =  MutableLiveData<MutableList<String>>()
    val lessonCategoryList: LiveData<MutableList<String>> = _lessonCategoryList

    private val _lessonSiteList =  MutableLiveData<MutableList<String>>()
    val lessonSiteList: LiveData<MutableList<String>> = _lessonSiteList

    init {
        viewModelScope.launch {
            fetchLessonCategoryList()
            fetchLessonSiteList()
        }
    }

    fun setLessonUpdateInfo(lessonDetail: LessonDetail) = with(lessonDetail) {
        _lessonDetail.value = LessonDetail(
            alertDays,
            categoryName,
            currentProgressRate,
            endDate,
            goalProgressRate,
            isFinished,
            lessonId,
            lessonName,
            message,
            presentNumber,
            price,
            remainDay,
            siteName,
            startDate,
            totalNumber,
            wastePrice
        )
    }

    fun updateLesson(
        lessonId: String,
        updateLessonRequest: LessonUpdateRequest,
    ) = viewModelScope.launch {
        _lessonUpdateState.value = LessonUpdateState.Loading
        val lessonUpdateResponse = lessonRepository.updateLesson(lessonId, updateLessonRequest)
        _lessonUpdateState.value = lessonUpdateResponse
    }

    private suspend fun fetchLessonCategoryList() {
        _lessonCategoryListState.value = LessonState.Loading
        val lessonCategoryListResponse = lessonRepository.fetchLessonCategoryList()
        _lessonCategoryListState.value = lessonCategoryListResponse
    }

    private suspend fun fetchLessonSiteList() {
        _lessonSiteListState.value = LessonState.Loading
        val lessonSiteListResponse = lessonRepository.fetchLessonSiteList()
        _lessonSiteListState.value = lessonSiteListResponse
    }
}