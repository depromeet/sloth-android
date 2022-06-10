package com.depromeet.sloth.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.model.Lesson
import com.depromeet.sloth.data.network.lesson.LessonRepository
import com.depromeet.sloth.data.network.lesson.category.LessonCategoryResponse
import com.depromeet.sloth.data.network.lesson.list.LessonState
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterResponse
import com.depromeet.sloth.data.network.lesson.site.LessonSiteResponse
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterLessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    memberRepository: MemberRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(memberRepository) {

    private val _lessonName = savedStateHandle.getLiveData<String>(KEY_LESSON_NAME, "")
    val lessonName: LiveData<String>
        get() = _lessonName

    private val _lessonTotalNumber = savedStateHandle.getLiveData<Int>(KEY_LESSON_TOTAL_NUMBER, 0)
    val lessonTotalNumber: LiveData<Int>
        get() = _lessonTotalNumber

    private val _lessonCategoryId = savedStateHandle.getLiveData<Int>(KEY_LESSON_CATEGORY_ID, 0)
    val lessonCategoryId: LiveData<Int>
        get() = _lessonCategoryId

    private val _lessonCategoryName = savedStateHandle.getLiveData<String>(KEY_LESSON_CATEGORY_NAME, "")
    val lessonCategoryName: LiveData<String>
        get() = _lessonCategoryName

    private val _lessonSiteId = savedStateHandle.getLiveData<Int>(KEY_LESSON_SITE_ID, 0)
    val lessonSiteId: LiveData<Int>
        get() = _lessonSiteId

    private val _lessonSiteName = savedStateHandle.getLiveData<String>(KEY_LESSON_SITE_NAME, "")
    val lessonSiteName: LiveData<String>
        get() = _lessonSiteName

    private val _lessonMessage = savedStateHandle.getLiveData<String>(KEY_LESSON_MESSAGE, "")
    val lessonMessage: LiveData<String>
        get() = _lessonMessage

    private val _lessonCategoryListState =
        MutableLiveData<Event<LessonState<List<LessonCategoryResponse>>>>()
    val lessonCategoryListState: LiveData<Event<LessonState<List<LessonCategoryResponse>>>>
        get() = _lessonCategoryListState

    private val _lessonSiteListState =
        MutableLiveData<Event<LessonState<List<LessonSiteResponse>>>>()
    val lessonSiteListState: LiveData<Event<LessonState<List<LessonSiteResponse>>>>
        get() = _lessonSiteListState

    private val _lessonRegisterState = MutableLiveData<Event<LessonState<LessonRegisterResponse>>>()
    val lessonRegisterState: LiveData<Event<LessonState<LessonRegisterResponse>>>
        get() = _lessonRegisterState

    private val _lessonCheck = MutableLiveData<Lesson>()
    val lessonCheck: LiveData<Lesson>
        get() = _lessonCheck

    private val _lessonRegister = MutableLiveData<LessonRegisterRequest>()
    val lessonRegister: LiveData<LessonRegisterRequest>
        get() = _lessonRegister

    private val _lessonCategorySelectedItemPosition = MutableLiveData<Int>()
    val lessonCategorySelectedItemPosition: LiveData<Int>
        get() = _lessonCategorySelectedItemPosition

    private val _lessonSiteSelectedItemPosition = MutableLiveData<Int>()
    val lessonSiteSelectedItemPosition: LiveData<Int>
        get() = _lessonSiteSelectedItemPosition

    private val _lesson = MutableLiveData<Lesson>()
    val lesson: LiveData<Lesson>
        get() = _lesson

    init {
        viewModelScope.launch {
            fetchLessonCategoryList()
            fetchLessonSiteList()
        }
    }

    fun setLessonCheckInfo(lesson: Lesson) = with(lesson) {
        _lessonCheck.value = Lesson(
            alertDays,
            categoryName,
            endDate,
            lessonName,
            message,
            price,
            siteName,
            startDate,
            totalNumber
        )
    }

    fun setLessonRegisterInfo(lessonRegisterInfo: LessonRegisterRequest)  {
        _lessonRegister.value = lessonRegisterInfo
    }

    fun registerLesson(
    ) = viewModelScope.launch {
        _lessonRegisterState.value = Event(LessonState.Loading)
        val lessonRegisterResponse = lessonRepository.registerLesson(lessonRegister.value!!)
        _lessonRegisterState.value = Event(lessonRegisterResponse)
    }

    private suspend fun fetchLessonCategoryList() {
        _lessonCategoryListState.value = Event(LessonState.Loading)
        val lessonCategoryListResponse = lessonRepository.fetchLessonCategoryList()
        _lessonCategoryListState.value = Event(lessonCategoryListResponse)
    }

    private suspend fun fetchLessonSiteList() {
        _lessonSiteListState.value = Event(LessonState.Loading)
        val lessonSiteListResponse = lessonRepository.fetchLessonSiteList()
        _lessonSiteListState.value = Event(lessonSiteListResponse)
    }

    fun setLessonCategoryItemPosition(position: Int) {
        _lessonCategorySelectedItemPosition.value = position
    }

    fun setLessonSiteItemPosition(position: Int) {
        _lessonSiteSelectedItemPosition.value = position
    }

    fun setLessonName(lessonName: String?) {
        if (this.lessonName.value == lessonName || lessonName == null) {
            return
        }
        savedStateHandle.set(KEY_LESSON_NAME, lessonName)
    }

    fun setLessonTotalNumber(lessonTotalNumber: Int?) {
        if (this.lessonTotalNumber.value == lessonTotalNumber || lessonTotalNumber == null) {
            return
        }
        savedStateHandle.set(KEY_LESSON_NAME, lessonName)
    }

    fun setCategoryId(lessonCategoryId: Int?) {
        if (this.lessonCategoryId.value == lessonCategoryId || lessonCategoryId == null) {
            return
        }
        savedStateHandle.set(KEY_LESSON_CATEGORY_ID, lessonCategoryId)
    }

    fun setCategoryName(lessonCategoryName: String?) {
        if (this.lessonCategoryName.value == lessonCategoryName || lessonCategoryName == null) {
            return
        }

        savedStateHandle.set(KEY_LESSON_CATEGORY_NAME, lessonCategoryName)
    }

    fun setSiteId(lessonSiteId: Int?) {
        if (this.lessonSiteId.value == lessonSiteId || lessonSiteId == null) {
            return
        }

        savedStateHandle.set(KEY_LESSON_SITE_ID, lessonSiteId)
    }

    fun setSiteName(lessonSiteName: String?) {
        if (this.lessonSiteName.value == lessonSiteName || lessonSiteName == null) {
            return
        }

        savedStateHandle.set(KEY_LESSON_SITE_NAME, lessonSiteName)
    }

    fun setLessonMessage(lessonMessage: String?) {
        if (this.lessonMessage.value == lessonMessage || lessonMessage == null) {
            return
        }
        savedStateHandle.set(KEY_LESSON_MESSAGE, lessonMessage)
    }

    companion object {
        const val KEY_LESSON_NAME = "lessonName"
        const val KEY_LESSON_TOTAL_NUMBER = "lessonCount"
        const val KEY_LESSON_CATEGORY_NAME = "lessonCategoryName"
        const val KEY_LESSON_CATEGORY_ID = "lessonCategoryId"
        const val KEY_LESSON_SITE_NAME = "lessonSiteName"
        const val KEY_LESSON_SITE_ID = "lessonSiteId"
        const val KEY_LESSON_START_DATE = "lessonStartDate"
        const val KEY_LESSON_END_DATE = "lessonEndDate"
        const val KEY_LESSON_PRICE = "lessonPrice"
        const val KEY_LESSON_MESSAGE = "lessonMessage"
    }
}