package com.depromeet.sloth.ui.register

import androidx.lifecycle.*
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.Lesson
import com.depromeet.sloth.data.network.lesson.LessonCategory
import com.depromeet.sloth.data.network.lesson.LessonSite
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterResponse
import com.depromeet.sloth.data.repository.LessonRepository
import com.depromeet.sloth.data.repository.MemberRepository
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.extensions.addSourceList
import com.depromeet.sloth.extensions.changeDateStringToArrayList
import com.depromeet.sloth.extensions.getMutableStateFlow
import com.depromeet.sloth.extensions.getPickerDateToDash
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.common.UiState
import com.depromeet.sloth.util.CALENDAR_TIME_ZONE
import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

//TODO repository 를 flow 로 감쌀 필요가 없다 뷰모델에서 liveData -> StateFlow 바꿔 주면 된다 viewModel 에서는 suspend 그대로
//TODO !! 처리한 변수들 보안 처리
@HiltViewModel
class RegisterLessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    memberRepository: MemberRepository,
    private val savedStateHandle: SavedStateHandle,
    private val stringResourcesProvider: StringResourcesProvider
) : BaseViewModel(memberRepository) {

    private val _registerLessonState = MutableSharedFlow<UiState<LessonRegisterResponse>>()
    val registerLessonState: SharedFlow<UiState<LessonRegisterResponse>>
        get() = _registerLessonState

    private val _lessonCategoryListState =
        MutableStateFlow<UiState<List<LessonCategory>>>(UiState.Loading)
    val lessonCategoryListState: StateFlow<UiState<List<LessonCategory>>> =
        _lessonCategoryListState.asStateFlow()

    private val _lessonSiteListState =
        MutableStateFlow<UiState<List<LessonSite>>>(UiState.Loading)
    val lessonSiteListState: StateFlow<UiState<List<LessonSite>>> =
        _lessonSiteListState.asStateFlow()

    private val _lessonName =
        savedStateHandle.getLiveData(KEY_LESSON_NAME, DEFAULT_STRING_VALUE)
    val lessonName: LiveData<String>
        get() = _lessonName

    private val _lessonTotalNumber = savedStateHandle.getLiveData(KEY_LESSON_TOTAL_NUMBER, 0)
    val lessonTotalNumber: LiveData<Int>
        get() = _lessonTotalNumber

    private val _lessonCategoryId = savedStateHandle.getMutableStateFlow(KEY_LESSON_CATEGORY_ID, 0)
    val lessonCategoryId: StateFlow<Int> = _lessonCategoryId.asStateFlow()

    private val _lessonCategoryName = savedStateHandle.getMutableStateFlow(KEY_LESSON_CATEGORY_NAME, DEFAULT_STRING_VALUE)
    val lessonCategoryName: StateFlow<String> = _lessonCategoryName.asStateFlow()

    private val _lessonSiteId = savedStateHandle.getMutableStateFlow(KEY_LESSON_SITE_ID, 0)
    val lessonSiteId: StateFlow<Int> = _lessonSiteId.asStateFlow()

    private val _lessonSiteName = savedStateHandle.getMutableStateFlow(KEY_LESSON_SITE_NAME, DEFAULT_STRING_VALUE)
    val lessonSiteName: StateFlow<String> = _lessonSiteName.asStateFlow()

    private val _lessonPrice = savedStateHandle.getMutableStateFlow(KEY_LESSON_PRICE, 0)
    val lessonPrice: StateFlow<Int>
        get() = _lessonPrice.asStateFlow()

    private val _lessonMessage =
        savedStateHandle.getMutableStateFlow(KEY_LESSON_MESSAGE, DEFAULT_STRING_VALUE)
    val lessonMessage: StateFlow<String>
        get() = _lessonMessage.asStateFlow()

    private val _lessonCheck = MutableStateFlow(Lesson())
    val lessonCheck: StateFlow<Lesson> = _lessonCheck.asStateFlow()

    private val _lessonCategorySelectedItemPosition = savedStateHandle.getLiveData(
        KEY_LESSON_CATEGORY_SELECTED_ITEM_POSITION, 0
    )
    val lessonCategorySelectedItemPosition: LiveData<Int>
        get() = _lessonCategorySelectedItemPosition

    private val _lessonSiteSelectedItemPosition = savedStateHandle.getLiveData(
        KEY_LESSON_SITE_SELECTED_ITEM_POSITION, 0
    )
    val lessonSiteSelectedItemPosition: LiveData<Int>
        get() = _lessonSiteSelectedItemPosition

    private val _startDate = savedStateHandle.getMutableStateFlow(KEY_START_DATE, Date())
    val startDate: StateFlow<Date> = _startDate.asStateFlow()

    private val _endDate = savedStateHandle.getMutableStateFlow(KEY_END_DATE, Date())
    val endDate: StateFlow<Date> = _endDate.asStateFlow()

    private val _lessonStartDate = savedStateHandle.getMutableStateFlow(KEY_LESSON_START_DATE, DEFAULT_STRING_VALUE)
    val lessonStartDate: StateFlow<String> = _lessonStartDate.asStateFlow()

    private val _lessonEndDate = savedStateHandle.getMutableStateFlow(KEY_LESSON_END_DATE, DEFAULT_STRING_VALUE)
    val lessonEndDate: StateFlow<String> = _lessonEndDate.asStateFlow()

    private val _lessonEndDateSelectedItemPosition = savedStateHandle.getLiveData(
        KEY_LESSON_END_DATE_SELECTED_ITEM_POSITION, 0
    )
    val lessonEndDateSelectedItemPosition: LiveData<Int>
        get() = _lessonEndDateSelectedItemPosition

    private val _lessonEndDateSelectedState = MutableLiveData<Boolean>()
    private val lessonEndDateSelectedState: LiveData<Boolean>
        get() = _lessonEndDateSelectedState

    private val _lessonCategoryMap = MutableStateFlow<HashMap<Int, String>>(hashMapOf())
    val lessonCategoryMap: StateFlow<HashMap<Int, String>> = _lessonCategoryMap.asStateFlow()

    private val _lessonSiteMap = MutableStateFlow<HashMap<Int, String>>(hashMapOf())
    val lessonSiteMap: StateFlow<HashMap<Int, String>> = _lessonSiteMap.asStateFlow()

    private val _lessonCategoryList = MutableStateFlow<List<String>>(mutableListOf())
    val lessonCategoryList: StateFlow<List<String>> = _lessonCategoryList.asStateFlow()

    private val _lessonSiteList = MutableStateFlow<List<String>>(mutableListOf())
    val lessonSiteList: StateFlow<List<String>> = _lessonSiteList.asStateFlow()

    private val _navigateToRegisterLessonSecond = MutableSharedFlow<Unit>()
    val navigateToRegisterLessonSecond: SharedFlow<Unit>
        get() = _navigateToRegisterLessonSecond

    private val _navigateToRegisterLessonCheck = MutableSharedFlow<Unit>()
    val navigateToRegisterLessonCheck: SharedFlow<Unit>
        get() = _navigateToRegisterLessonCheck

    private val _lessonDateRangeValidation = MutableLiveData<Boolean>()
    val lessonDateRangeValidation: LiveData<Boolean>
        get() = _lessonDateRangeValidation

    private val _registerLessonStartDate = MutableSharedFlow<Date>()
    val registerLessonStartDate: SharedFlow<Date>
        get() = _registerLessonStartDate

    private val _registerLessonEndDate = MutableSharedFlow<Date>()
    val registerLessonEndDate: SharedFlow<Date>
        get() = _registerLessonEndDate

    val navigateToLessonSecondButtonState = MediatorLiveData<Boolean>().apply {
        addSourceList(
            _lessonName,
            _lessonTotalNumber,
            _lessonCategorySelectedItemPosition,
            _lessonSiteSelectedItemPosition
        ) {
            canNavigateToLessonSecond()
        }
    }

    val navigateToLessonCheckButtonState = MediatorLiveData<Boolean>().apply {
        addSourceList(
            _lessonEndDateSelectedState,
            _lessonDateRangeValidation
        ) {
            canNavigateToLessonCheck()
        }
    }

    private fun initLessonStartDate() {
        val today = Date()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(CALENDAR_TIME_ZONE))
        calendar.time = today
        _startDate.value = calendar.time
        _lessonStartDate.value = getPickerDateToDash(calendar.time)
    }

    //async await 빼라 했던거 같은데 현우님이
    init {
        viewModelScope.launch {
            val lessonCategoryListResponse = async {
                lessonRepository.fetchLessonCategoryList()
            }
            val lessonSiteListResponse = async {
                lessonRepository.fetchLessonSiteList()
            }
            _lessonCategoryListState.value = lessonCategoryListResponse.await()
            _lessonSiteListState.value = lessonSiteListResponse.await()
        }
        initLessonStartDate()
    }

    fun setLessonStartDate(calendar: Calendar) {
        setStartDate(calendar.time)
        setLessonStartDate(getPickerDateToDash(startDate.value))
        setLessonDateRangeValidation()
    }

    fun setLessonEndDateBySpinner(position: Int?) {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(CALENDAR_TIME_ZONE))
        calendar.time = startDate.value
        when (position) {
            ONE_WEEK -> calendar.add(Calendar.DATE, 7)
            ONE_MONTH -> calendar.add(Calendar.MONTH, 1)
            TWO_MONTH -> calendar.add(Calendar.MONTH, 2)
            THREE_MONTH -> calendar.add(Calendar.MONTH, 3)
            else -> Unit
        }
        setEndDate(calendar.time)
        setLessonEndDate(getPickerDateToDash(calendar.time))
        setLessonDateRangeValidation()
    }

    fun setLessonEndDateByCalendar(calendar: Calendar) {
        setEndDate(calendar.time)
        setLessonEndDate(getPickerDateToDash(calendar.time))
        setLessonDateRangeValidation()
    }

    fun setLessonCategoryList(data: List<LessonCategory>) {
        _lessonCategoryMap.value =
            data.map { it.categoryId to it.categoryName }.toMap() as HashMap<Int, String>
        _lessonCategoryList.value = data.map { it.categoryName }.toMutableList().apply {
            add(0, stringResourcesProvider.getString(R.string.choose_lesson_category))
        }
    }

    fun setLessonSiteList(data: List<LessonSite>) {
        _lessonSiteMap.value =
            data.map { it.siteId to it.siteName }.toMap() as HashMap<Int, String>
        _lessonSiteList.value = data.map { it.siteName }.toMutableList().apply {
            add(0, stringResourcesProvider.getString(R.string.choose_lesosn_site))
        }
    }

    private fun setLessonDateRangeValidation() {
        _lessonDateRangeValidation.value = startDate.value <= endDate.value
    }

    fun setLessonInfo() {
        _lessonCheck.value = Lesson(
            categoryName = lessonCategoryName.value,
            endDate = changeDateStringToArrayList(lessonEndDate.value),
            lessonName = lessonName.value!!,
            message = lessonMessage.value,
            price = lessonPrice.value,
            siteName = lessonSiteName.value,
            startDate = changeDateStringToArrayList(lessonStartDate.value),
            totalNumber = lessonTotalNumber.value!!
        )
    }

    fun registerLesson() = viewModelScope.launch {
        _registerLessonState.emit(UiState.Loading)
        _registerLessonState.emit(
            lessonRepository.registerLesson(
                LessonRegisterRequest(
                    alertDays = null,
                    categoryId = lessonCategoryId.value,
                    endDate = lessonEndDate.value,
                    lessonName = lessonName.value!!,
                    message = lessonMessage.value,
                    price = lessonPrice.value,
                    siteId = lessonSiteId.value,
                    startDate = lessonStartDate.value,
                    totalNumber = lessonTotalNumber.value!!
                )
            )
        )
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
        _lessonEndDateSelectedState.value = lessonEndDateSelectedItemPosition.value != 0
    }

    fun setLessonName(lessonName: String?) {
        if (this.lessonName.value == lessonName || lessonName == null) {
            return
        }
        savedStateHandle[KEY_LESSON_NAME] = lessonName
    }

    fun setLessonTotalNumber(lessonTotalNumber: Int?) {
        if (this.lessonTotalNumber.value == lessonTotalNumber || lessonTotalNumber == null) {
            return
        }
        savedStateHandle[KEY_LESSON_TOTAL_NUMBER] = lessonTotalNumber
    }

    fun setCategoryId(lessonCategoryId: Int?) {
        if (this.lessonCategoryId.value == lessonCategoryId || lessonCategoryId == null) {
            return
        }
        savedStateHandle[KEY_LESSON_CATEGORY_ID] = lessonCategoryId
    }

    fun setCategoryName(lessonCategoryName: String?) {
        if (this.lessonCategoryName.value == lessonCategoryName || lessonCategoryName == null) {
            return
        }
        savedStateHandle[KEY_LESSON_CATEGORY_NAME] = lessonCategoryName
    }

    fun setSiteId(lessonSiteId: Int?) {
        if (this.lessonSiteId.value == lessonSiteId || lessonSiteId == null) {
            return
        }
        savedStateHandle[KEY_LESSON_SITE_ID] = lessonSiteId
    }

    fun setSiteName(lessonSiteName: String?) {
        if (this.lessonSiteName.value == lessonSiteName || lessonSiteName == null) {
            return
        }
        savedStateHandle[KEY_LESSON_SITE_NAME] = lessonSiteName
    }

    fun setLessonPrice(lessonPrice: Int?) {
        if (this.lessonPrice.value == lessonPrice || lessonPrice == null) {
            return
        }
        savedStateHandle[KEY_LESSON_PRICE] = lessonPrice
    }

    fun setLessonMessage(lessonMessage: String?) {
        if (this.lessonMessage.value == lessonMessage || lessonMessage == null) {
            return
        }
        savedStateHandle[KEY_LESSON_MESSAGE] = lessonMessage
    }

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

    private fun setLessonStartDate(lessonStartDate: String?) {
        if (this.lessonStartDate.value == lessonStartDate || lessonStartDate == null) {
            return
        }
        savedStateHandle[KEY_LESSON_START_DATE] = lessonStartDate
    }

    private fun setLessonEndDate(lessonEndDate: String?) {
        if (this.lessonEndDate.value == lessonEndDate || lessonEndDate == null) {
            return
        }
        savedStateHandle[KEY_LESSON_END_DATE] = lessonEndDate
    }

    fun navigateToRegisterLessonSecond() = viewModelScope.launch {
        _navigateToRegisterLessonSecond.emit(Unit)
    }

    fun navigateToRegisterLessonCheck() = viewModelScope.launch {
        _navigateToRegisterLessonCheck.emit(Unit)
    }

    fun registerLessonStartDate() = viewModelScope.launch {
        _registerLessonStartDate.emit(startDate.value)
    }

    fun registerLessonEndDate() = viewModelScope.launch {
        _registerLessonEndDate.emit(endDate.value)
    }

    private fun canNavigateToLessonSecond() =
        !lessonName.value.isNullOrBlank() && lessonTotalNumber.value != 0 &&
                lessonCategorySelectedItemPosition.value != 0 && lessonSiteSelectedItemPosition.value != 0

    private fun canNavigateToLessonCheck() =
        lessonEndDateSelectedState.value ?: false && lessonDateRangeValidation.value ?: false

    companion object {
        const val KEY_LESSON_NAME = "lessonName"
        const val KEY_LESSON_TOTAL_NUMBER = "lessonCount"
        const val KEY_LESSON_CATEGORY_NAME = "lessonCategoryName"
        const val KEY_LESSON_CATEGORY_ID = "lessonCategoryId"
        const val KEY_LESSON_CATEGORY_SELECTED_ITEM_POSITION = "lessonCategorySelectedItemPosition"
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
        const val ONE_WEEK = 1
        const val ONE_MONTH = 2
        const val TWO_MONTH = 3
        const val THREE_MONTH = 4
        const val CUSTOM_SETTING = 5
    }
}