package com.depromeet.sloth.ui.register

import androidx.lifecycle.*
import com.depromeet.sloth.data.model.Lesson
import com.depromeet.sloth.data.model.LessonCategory
import com.depromeet.sloth.data.model.LessonSite
import com.depromeet.sloth.data.repository.LessonRepository
import com.depromeet.sloth.data.network.lesson.LessonState
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterResponse
import com.depromeet.sloth.data.repository.MemberRepository
import com.depromeet.sloth.extensions.addSourceList
import com.depromeet.sloth.extensions.changeDateStringToArrayList
import com.depromeet.sloth.extensions.getPickerDateToDash
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RegisterLessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    memberRepository: MemberRepository,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel(memberRepository) {

    private val _lessonCategoryListState =
        MutableLiveData<LessonState<List<LessonCategory>>>()
    val lessonCategoryListState: LiveData<LessonState<List<LessonCategory>>>
        get() = _lessonCategoryListState

    private val _lessonSiteListState =
        MutableLiveData<LessonState<List<LessonSite>>>()
    val lessonSiteListState: LiveData<LessonState<List<LessonSite>>>
        get() = _lessonSiteListState

    private val _lessonName =
        savedStateHandle.getLiveData<String>(KEY_LESSON_NAME, DEFAULT_STRING_VALUE)
    val lessonName: LiveData<String>
        get() = _lessonName

    private val _lessonTotalNumber = savedStateHandle.getLiveData<Int>(KEY_LESSON_TOTAL_NUMBER, 0)
    val lessonTotalNumber: LiveData<Int>
        get() = _lessonTotalNumber

    private val _lessonCategoryId = savedStateHandle.getLiveData<Int>(KEY_LESSON_CATEGORY_ID, 0)
    val lessonCategoryId: LiveData<Int>
        get() = _lessonCategoryId

    private val _lessonCategoryName =
        savedStateHandle.getLiveData<String>(KEY_LESSON_CATEGORY_NAME, DEFAULT_STRING_VALUE)
    val lessonCategoryName: LiveData<String>
        get() = _lessonCategoryName

    private val _lessonSiteId = savedStateHandle.getLiveData<Int>(KEY_LESSON_SITE_ID, 0)
    val lessonSiteId: LiveData<Int>
        get() = _lessonSiteId

    private val _lessonSiteName =
        savedStateHandle.getLiveData<String>(KEY_LESSON_SITE_NAME, DEFAULT_STRING_VALUE)
    val lessonSiteName: LiveData<String>
        get() = _lessonSiteName

    private val _lessonPrice = savedStateHandle.getLiveData<Int>(KEY_LESSON_PRICE, 0)
    val lessonPrice: LiveData<Int>
        get() = _lessonPrice

    private val _lessonMessage =
        savedStateHandle.getLiveData<String>(KEY_LESSON_MESSAGE, DEFAULT_STRING_VALUE)
    val lessonMessage: LiveData<String>
        get() = _lessonMessage

    private val _lessonCategoryMap = savedStateHandle.getLiveData<HashMap<Int, String>>(
        KEY_LESSON_CATEGORY_MAP, HashMap<Int, String>())
    val lessonCategoryMap: LiveData<HashMap<Int, String>>
        get() = _lessonCategoryMap

    private val _lessonCategoryList = savedStateHandle.getLiveData<MutableList<String>>(
        KEY_LESSON_CATEGORY_LIST, mutableListOf())
    val lessonCategoryList: LiveData<MutableList<String>>
        get() = _lessonCategoryList

    private val _lessonSiteMap = savedStateHandle.getLiveData<HashMap<Int, String>>(
        KEY_LESSON_SITE_MAP, HashMap<Int, String>())
    val lessonSiteMap: LiveData<HashMap<Int, String>>
        get() = _lessonSiteMap

    private val _lessonSiteList = savedStateHandle.getLiveData<MutableList<String>>(
        KEY_LESSON_SITE_LIST, mutableListOf())
    val lessonSiteList: LiveData<MutableList<String>>
        get() = _lessonSiteList

    private val _lessonRegisterState = MutableLiveData<Event<LessonState<LessonRegisterResponse>>>()
    val lessonRegisterState: LiveData<Event<LessonState<LessonRegisterResponse>>>
        get() = _lessonRegisterState

    private val _lessonCheck = MutableLiveData<Lesson>()
    val lessonCheck: LiveData<Lesson>
        get() = _lessonCheck

    private val _lessonRegister = MutableLiveData<LessonRegisterRequest>()
    val lessonRegister: LiveData<LessonRegisterRequest>
        get() = _lessonRegister

    private val _lessonCategorySelectedItemPosition = savedStateHandle.getLiveData<Int>(
        KEY_LESSON_CATEGORY_SELECTED_ITEM_POSITION, 0)
    val lessonCategorySelectedItemPosition: LiveData<Int>
        get() = _lessonCategorySelectedItemPosition

    private val _lessonSiteSelectedItemPosition = savedStateHandle.getLiveData<Int>(
        KEY_LESSON_SITE_SELECTED_ITEM_POSITION, 0)
    val lessonSiteSelectedItemPosition: LiveData<Int>
        get() = _lessonSiteSelectedItemPosition

    private val _startDate = savedStateHandle.getLiveData<Date>(
        KEY_START_DATE)
    val startDate: LiveData<Date>
        get() = _startDate

    private val _endDate = savedStateHandle.getLiveData<Date>(
        KEY_END_DATE)
    val endDate: LiveData<Date>
        get() = _endDate

    private val _lessonStartDate = savedStateHandle.getLiveData<String>(
        KEY_LESSON_START_DATE, DEFAULT_STRING_VALUE)
    val lessonStartDate: LiveData<String>
        get() = _lessonStartDate

    private val _lessonEndDate = savedStateHandle.getLiveData<String>(
        KEY_LESSON_END_DATE, DEFAULT_STRING_VALUE)
    val lessonEndDate: LiveData<String>
        get() = _lessonEndDate

    private val _lessonEndDateSelectedItemPosition = savedStateHandle.getLiveData<Int>(
        KEY_LESSON_END_DATE_SELECTED_ITEM_POSITION, 0)
    val lessonEndDateSelectedItemPosition: LiveData<Int>
        get() = _lessonEndDateSelectedItemPosition

    private val _moveRegisterLessonSecondEvent = MutableLiveData<Event<Boolean>>()
    val moveRegisterLessonSecondEvent: LiveData<Event<Boolean>>
        get() = _moveRegisterLessonSecondEvent

    private val _moveRegisterLessonCheckEvent = MutableLiveData<Event<Boolean>>()
    val moveRegisterLessonCheckEvent: LiveData<Event<Boolean>>
        get() = _moveRegisterLessonCheckEvent

    private val _lessonDateValidation = MutableLiveData<Boolean>()
    val lessonDateValidation: LiveData<Boolean>
        get() = _lessonDateValidation

    private val _navigateToStartDate = MutableLiveData<Event<Date>>()
    val navigateToStartDate: LiveData<Event<Date>>
        get() = _navigateToStartDate

    private val _navigateToEndDate = MutableLiveData<Event<Date>>()
    val navigateToEndDate: LiveData<Event<Date>>
        get() = _navigateToEndDate

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

    private fun initLessonStartDate() {
        val today = Date()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
        calendar.time = today
        _startDate.value = calendar.time
        _lessonStartDate.value = getPickerDateToDash(calendar.time)
    }

    init {
        viewModelScope.launch {
            fetchLessonCategoryList()
            fetchLessonSiteList()
        }
        initLessonStartDate()
    }

    fun updateLessonStartDate(calendar: Calendar) {
        setStartDate(calendar.time)
        setLessonStartDate(getPickerDateToDash(startDate.value!!))
    }

    fun updateLessonEndDateBySpinner(position: Int?) {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
        calendar.time = startDate.value!!
        when (position) {
            ONE_WEEK -> {
                calendar.add(Calendar.DATE, 7)
            }

            ONE_MONTH -> {
                calendar.add(Calendar.MONTH, 1)
            }

            TWO_MONTH -> {
                calendar.add(Calendar.MONTH, 2)
            }

            THREE_MONTH -> {
                calendar.add(Calendar.MONTH, 3)
            }
            else -> Unit
        }
        setEndDate(calendar.time)
        setLessonEndDate(getPickerDateToDash(calendar.time))
    }

    fun updateLessonEndDateByCalendar(calendar: Calendar) {
        setEndDate(calendar.time)
        setLessonEndDate(getPickerDateToDash(calendar.time))
    }


    fun setLessonCategoryList(data: List<LessonCategory>) {
        _lessonCategoryMap.value =
                //data.map { it.categoryId to it.categoryName }.toMap() as HashMap<Int, String>
            data.associate { it.categoryId to it.categoryName } as HashMap<Int, String>
        _lessonCategoryList.value = data.map { it.categoryName }.toMutableList().apply {
            add(0, "강의 카테고리를 선택해 주세요")
        }
    }

    fun setLessonSiteList(data: List<LessonSite>) {
        _lessonSiteMap.value =
                //data.map { it.siteId to it.siteName }.toMap() as HashMap<Int, String>
            data.associate { it.siteId to it.siteName } as HashMap<Int, String>
        _lessonSiteList.value = data.map { it.siteName }.toMutableList().apply {
            add(0, "강의 사이트를 선택해 주세요")
        }
    }

//    val lessonCategoryList: StateFlow<UIState<List<LessonCategory>>> =
//        lessonRepository.fetchLessonCategoryList()
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(5000L),
//                initialValue = UIState.Loading
//            )
//
//    val lessonSiteCategoryList: StateFlow<UIState<List<LessonSite>>> =
//        lessonRepository.fetchLessonSiteList()
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(5000L),
//                initialValue = UIState.Loading
//            )

    fun setLessonInfo() {
        if (startDate.value!! > endDate.value!!) {
            _lessonDateValidation.value = false
        }
        _lessonCheck.value = Lesson(
            categoryName = lessonCategoryName.value!!,
            endDate = changeDateStringToArrayList(lessonEndDate.value!!),
            lessonName = lessonName.value!!,
            message = lessonMessage.value,
            price = lessonPrice.value!!,
            siteName = lessonSiteName.value!!,
            startDate = changeDateStringToArrayList(lessonStartDate.value!!),
            totalNumber = lessonTotalNumber.value!!
        )

        _lessonRegister.value = LessonRegisterRequest(
            alertDays = null,
            categoryId = lessonCategoryId.value!!,
            endDate = lessonEndDate.value!!,
            lessonName = lessonName.value!!,
            message = lessonMessage.value!!,
            price = lessonPrice.value!!,
            siteId = lessonSiteId.value!!,
            startDate = lessonStartDate.value!!,
            totalNumber = lessonTotalNumber.value!!
        )
    }

    fun registerLesson(
    ) = viewModelScope.launch {
        _lessonRegisterState.value = Event(LessonState.Loading)
        val lessonRegisterResponse = lessonRepository.registerLesson(lessonRegister.value!!)
        _lessonRegisterState.value = Event(lessonRegisterResponse)
    }

    fun setLessonCategoryItemPosition(position: Int?) {
        if (this.lessonCategorySelectedItemPosition.value == position || position == null) {
            return
        }
        _lessonCategorySelectedItemPosition.value = position
    }

    fun setLessonSiteItemPosition(position: Int?) {
        if (this.lessonSiteSelectedItemPosition.value == position || position == null) {
            return
        }
        _lessonSiteSelectedItemPosition.value = position
    }

    fun setLessonEndDateSelectedItemPosition(position: Int?) {
        if (this.lessonEndDateSelectedItemPosition.value == position || position == null) {
            return
        }
        _lessonEndDateSelectedItemPosition.value = position
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
        savedStateHandle.set(KEY_LESSON_TOTAL_NUMBER, lessonTotalNumber)
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

    fun setLessonPrice(lessonPrice: Int?) {
        if (this.lessonPrice.value == lessonPrice || lessonPrice == null) {
            return
        }

        savedStateHandle.set(KEY_LESSON_PRICE, lessonPrice)
    }

    fun setLessonMessage(lessonMessage: String?) {
        if (this.lessonMessage.value == lessonMessage || lessonMessage == null) {
            return
        }
        savedStateHandle.set(KEY_LESSON_MESSAGE, lessonMessage)
    }

    private fun setLessonStartDate(lessonStartDate: String?) {
        if (this.lessonStartDate.value == lessonStartDate || lessonStartDate == null) {
            return
        }
        savedStateHandle.set(KEY_LESSON_START_DATE, lessonStartDate)
    }

    private fun setLessonEndDate(lessonEndDate: String?) {
        if (this.lessonEndDate.value == lessonEndDate || lessonEndDate == null) {
            return
        }
        savedStateHandle.set(KEY_LESSON_END_DATE, lessonEndDate)
    }

    fun moveRegisterLessonSecond() {
        _moveRegisterLessonSecondEvent.value = Event(true)
    }

    fun moveRegisterLessonCheck() {
        _moveRegisterLessonCheckEvent.value = Event(true)
    }

    fun navigateToStartDate() {
        _navigateToStartDate.value = Event(startDate.value ?: Date())
    }

    fun navigateToEndDate() {
        _navigateToEndDate.value = Event(endDate.value ?: Date())
    }

    val isEnabledMoveLessonSecondButton = MediatorLiveData<Boolean>().apply {
        addSourceList(
            _lessonName,
            _lessonTotalNumber,
            _lessonCategorySelectedItemPosition,
            _lessonSiteSelectedItemPosition
        ) {
            isValidLessonFirstEnterInfo()
        }
    }

    private fun isValidLessonFirstEnterInfo() =
        !_lessonName.value.isNullOrBlank() && _lessonTotalNumber.value != 0 &&
                _lessonCategorySelectedItemPosition.value != 0 && _lessonSiteSelectedItemPosition.value != 0

    private fun isValidLessonSecondEnterInfo() =
        _lessonEndDateSelectedItemPosition.value != 0

    private fun setStartDate(startDate: Date?) {
        if (this.startDate.value == startDate || startDate == null) {
            return
        }
        savedStateHandle[KEY_START_DATE] = startDate
    }

    private fun setEndDate(endDate: Date?) {
        if (this.endDate.value == endDate || endDate == null) {
            return
        }
        savedStateHandle[KEY_END_DATE] = endDate
    }

    companion object {
        const val KEY_LESSON_NAME = "lessonName"
        const val KEY_LESSON_TOTAL_NUMBER = "lessonCount"
        const val KEY_LESSON_CATEGORY_MAP = "lessonCategoryMap"
        const val KEY_LESSON_CATEGORY_LIST = "lessonCategoryList"
        const val KEY_LESSON_CATEGORY_NAME = "lessonCategoryName"
        const val KEY_LESSON_CATEGORY_ID = "lessonCategoryId"
        const val KEY_LESSON_CATEGORY_SELECTED_ITEM_POSITION = "lessonCategorySelectedItemPosition"
        const val KEY_LESSON_SITE_MAP = "lessonSiteMap"
        const val KEY_LESSON_SITE_LIST = "lessonSiteList"
        const val KEY_LESSON_SITE_NAME = "lessonSiteName"
        const val KEY_LESSON_SITE_ID = "lessonSiteId"
        const val KEY_LESSON_SITE_SELECTED_ITEM_POSITION = "lessonSiteSelectedItemPosition"
        const val KEY_START_DATE = "startDate"
        const val KEY_END_DATE = "endDate"
        const val KEY_LESSON_START_DATE = "lessonStartDate"
        const val KEY_LESSON_END_DATE = "lessonEndDate"
        const val KEY_LESSON_END_DATE_SELECTED_ITEM_POSITION = "lessonEndDateSelectedItemPosition"
        const val KEY_LESSON_PRICE = "lessonPrice"
        const val KEY_LESSON_MESSAGE = "lessonMessage"

        const val DAY = 86400000L
        const val DEFAULT = 0
        const val ONE_WEEK = 1
        const val ONE_MONTH = 2
        const val TWO_MONTH = 3
        const val THREE_MONTH = 4
        const val CUSTOM_SETTING = 5
        const val DEFAULT_STRING_VALUE = ""
    }
}