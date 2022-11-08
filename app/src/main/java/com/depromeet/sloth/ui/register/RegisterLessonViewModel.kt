package com.depromeet.sloth.ui.register

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.Lesson
import com.depromeet.sloth.data.network.lesson.LessonCategory
import com.depromeet.sloth.data.network.lesson.LessonSite
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterResponse
import com.depromeet.sloth.data.repository.LessonRepository
import com.depromeet.sloth.data.repository.MemberRepository
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.extensions.changeDateStringToArrayList
import com.depromeet.sloth.extensions.getMutableStateFlow
import com.depromeet.sloth.extensions.getPickerDateToDash
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.common.Result
import com.depromeet.sloth.util.CALENDAR_TIME_ZONE
import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RegisterLessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    savedStateHandle: SavedStateHandle,
    private val stringResourcesProvider: StringResourcesProvider,
    memberRepository: MemberRepository,
) : BaseViewModel(memberRepository) {

    private val _registerLessonState = MutableSharedFlow<Result<LessonRegisterResponse>>()
    val registerLessonState: SharedFlow<Result<LessonRegisterResponse>> =
        _registerLessonState.asSharedFlow()

    private val _lessonCategoryListState =
        MutableStateFlow<Result<List<LessonCategory>>>(Result.Loading)
    val lessonCategoryListState: StateFlow<Result<List<LessonCategory>>> =
        _lessonCategoryListState.asStateFlow()

    private val _lessonSiteListState =
        MutableStateFlow<Result<List<LessonSite>>>(Result.Loading)
    val lessonSiteListState: StateFlow<Result<List<LessonSite>>> =
        _lessonSiteListState.asStateFlow()

    private val _lessonName =
        savedStateHandle.getMutableStateFlow(KEY_LESSON_NAME, DEFAULT_STRING_VALUE)
    val lessonName: StateFlow<String> = _lessonName.asStateFlow()

    private val _lessonTotalNumber =
        savedStateHandle.getMutableStateFlow(KEY_LESSON_TOTAL_NUMBER, 0)
    val lessonTotalNumber: StateFlow<Int> = _lessonTotalNumber.asStateFlow()

    private val _lessonCategoryId = savedStateHandle.getMutableStateFlow(KEY_LESSON_CATEGORY_ID, 0)
    val lessonCategoryId: StateFlow<Int> = _lessonCategoryId.asStateFlow()

    private val _lessonCategoryName =
        savedStateHandle.getMutableStateFlow(KEY_LESSON_CATEGORY_NAME, DEFAULT_STRING_VALUE)
    val lessonCategoryName: StateFlow<String> = _lessonCategoryName.asStateFlow()

    private val _lessonCategorySelectedItemPosition = savedStateHandle.getMutableStateFlow(
        KEY_LESSON_CATEGORY_SELECTED_ITEM_POSITION, 0
    )
    val lessonCategorySelectedItemPosition: StateFlow<Int> =
        _lessonCategorySelectedItemPosition.asStateFlow()

    private val _lessonSiteId = savedStateHandle.getMutableStateFlow(KEY_LESSON_SITE_ID, 0)
    val lessonSiteId: StateFlow<Int> = _lessonSiteId.asStateFlow()

    private val _lessonSiteName =
        savedStateHandle.getMutableStateFlow(KEY_LESSON_SITE_NAME, DEFAULT_STRING_VALUE)
    val lessonSiteName: StateFlow<String> = _lessonSiteName.asStateFlow()

    private val _lessonSiteSelectedItemPosition = savedStateHandle.getMutableStateFlow(
        KEY_LESSON_SITE_SELECTED_ITEM_POSITION, 0
    )
    val lessonSiteSelectedItemPosition: StateFlow<Int> =
        _lessonSiteSelectedItemPosition.asStateFlow()

    private val _lessonPrice = savedStateHandle.getMutableStateFlow(KEY_LESSON_PRICE, 0)
    val lessonPrice: StateFlow<Int> = _lessonPrice.asStateFlow()

    private val _lessonMessage =
        savedStateHandle.getMutableStateFlow(KEY_LESSON_MESSAGE, DEFAULT_STRING_VALUE)
    val lessonMessage: StateFlow<String> = _lessonMessage.asStateFlow()

    private val _lesson = MutableStateFlow(Lesson())
    val lesson: StateFlow<Lesson> = _lesson.asStateFlow()

    private val _startDate = savedStateHandle.getMutableStateFlow(KEY_START_DATE, Date())
    val startDate: StateFlow<Date> = _startDate.asStateFlow()

    private val _endDate = savedStateHandle.getMutableStateFlow(KEY_END_DATE, Date())
    val endDate: StateFlow<Date> = _endDate.asStateFlow()

    private val _lessonStartDate =
        savedStateHandle.getMutableStateFlow(KEY_LESSON_START_DATE, DEFAULT_STRING_VALUE)
    val lessonStartDate: StateFlow<String> = _lessonStartDate.asStateFlow()

    private val _lessonEndDate =
        savedStateHandle.getMutableStateFlow(KEY_LESSON_END_DATE, DEFAULT_STRING_VALUE)
    val lessonEndDate: StateFlow<String> = _lessonEndDate.asStateFlow()

    private val _lessonEndDateSelectedItemPosition = savedStateHandle.getMutableStateFlow(
        KEY_LESSON_END_DATE_SELECTED_ITEM_POSITION, 0
    )
    val lessonEndDateSelectedItemPosition: StateFlow<Int> =
        _lessonEndDateSelectedItemPosition.asStateFlow()

    private val _lessonEndDateSelectedState = MutableStateFlow(false)
    private val lessonEndDateSelectedState: StateFlow<Boolean> =
        _lessonEndDateSelectedState.asStateFlow()

    private val _lessonCategoryMap = MutableStateFlow<HashMap<Int, String>>(hashMapOf())
    val lessonCategoryMap: StateFlow<HashMap<Int, String>> = _lessonCategoryMap.asStateFlow()

    private val _lessonSiteMap = MutableStateFlow<HashMap<Int, String>>(hashMapOf())
    val lessonSiteMap: StateFlow<HashMap<Int, String>> = _lessonSiteMap.asStateFlow()

    private val _lessonCategoryList = MutableStateFlow<List<String>>(mutableListOf())
    val lessonCategoryList: StateFlow<List<String>> = _lessonCategoryList.asStateFlow()

    private val _lessonSiteList = MutableStateFlow<List<String>>(mutableListOf())
    val lessonSiteList: StateFlow<List<String>> = _lessonSiteList.asStateFlow()

    private val _onNavigateToRegisterLessonSecondClick = MutableSharedFlow<Unit>()
    val onNavigateToRegisterLessonSecondClick: SharedFlow<Unit> =
        _onNavigateToRegisterLessonSecondClick.asSharedFlow()

    private val _onNavigateToRegisterLessonCheckClick = MutableSharedFlow<Unit>()
    val onNavigateToRegisterLessonCheckClick: SharedFlow<Unit> =
        _onNavigateToRegisterLessonCheckClick.asSharedFlow()

    private val _lessonDateRangeValidation = MutableStateFlow(true)
    val lessonDateRangeValidation: StateFlow<Boolean> = _lessonDateRangeValidation.asStateFlow()

    private val _onRegisterLessonStartDateClick = MutableSharedFlow<Date>()
    val onRegisterLessonStartDateClick: SharedFlow<Date> =
        _onRegisterLessonStartDateClick.asSharedFlow()

    private val _registerLessonEndDate = MutableSharedFlow<Date>()
    val registerLessonEndDate: SharedFlow<Date> = _registerLessonEndDate.asSharedFlow()

    val navigateToLessonSecondButtonState = combine(
        lessonName,
        lessonTotalNumber,
        lessonCategorySelectedItemPosition,
        lessonSiteSelectedItemPosition
    ) { name, totalNumber, categorySelectedItemPosition, siteSelectedItemPosition ->
        name.isNotBlank() && totalNumber != 0 &&
                categorySelectedItemPosition != 0 && siteSelectedItemPosition != 0
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    val navigateToLessonCheckButtonState = combine(
        lessonEndDateSelectedState,
        lessonDateRangeValidation
    ) { endDateSelectedState, dateRangeValidation ->
        endDateSelectedState && dateRangeValidation
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

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
        _startDate.value = calendar.time
        _lessonStartDate.value = getPickerDateToDash(startDate.value)
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
            CUSTOM_SETTING -> return
        }
        _endDate.value = calendar.time
        _lessonEndDate.value = getPickerDateToDash(calendar.time)
        setLessonDateRangeValidation()
    }

    fun setLessonEndDateByCalendar(calendar: Calendar) {
        _endDate.value = calendar.time
        _lessonEndDate.value = getPickerDateToDash(calendar.time)
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
        _lesson.value = Lesson(
            categoryName = lessonCategoryName.value,
            endDate = changeDateStringToArrayList(lessonEndDate.value),
            lessonName = lessonName.value,
            message = lessonMessage.value,
            price = lessonPrice.value,
            siteName = lessonSiteName.value,
            startDate = changeDateStringToArrayList(lessonStartDate.value),
            totalNumber = lessonTotalNumber.value
        )
    }

    fun registerLesson() = viewModelScope.launch {
        _registerLessonState.emit(Result.Loading)
        _registerLessonState.emit(
            lessonRepository.registerLesson(
                LessonRegisterRequest(
                    alertDays = null,
                    categoryId = lessonCategoryId.value,
                    endDate = lessonEndDate.value,
                    lessonName = lessonName.value,
                    message = lessonMessage.value,
                    price = lessonPrice.value,
                    siteId = lessonSiteId.value,
                    startDate = lessonStartDate.value,
                    totalNumber = lessonTotalNumber.value
                )
            )
        )
    }

    fun setLessonCategorySelectedItemPosition(position: Int) {
        _lessonCategorySelectedItemPosition.value = position
    }

    fun setLessonSiteItemPosition(position: Int) {
        _lessonSiteSelectedItemPosition.value = position
    }

    fun setLessonEndDateSelectedItemPosition(position: Int) {
        _lessonEndDateSelectedItemPosition.value = position
        _lessonEndDateSelectedState.value = lessonEndDateSelectedItemPosition.value != 0
    }

    fun setLessonName(lessonName: String) {
        _lessonName.value = lessonName
    }

    fun setLessonTotalNumber(lessonTotalNumber: Int) {
        _lessonTotalNumber.value = lessonTotalNumber
    }

    fun setLessonCategoryId(lessonCategoryId: Int) {
        _lessonCategoryId.value = lessonCategoryId
    }

    fun setLessonCategoryName(lessonCategoryName: String) {
        _lessonCategoryName.value = lessonCategoryName
    }

    fun setLessonSiteId(lessonSiteId: Int) {
        _lessonSiteId.value = lessonSiteId
    }

    fun setLessonSiteName(lessonSiteName: String) {
        _lessonSiteName.value = lessonSiteName
    }

    fun setLessonPrice(lessonPrice: Int) {
        _lessonPrice.value = lessonPrice
    }

    fun setLessonMessage(lessonMessage: String) {
        _lessonMessage.value = lessonMessage
    }

    fun navigateToRegisterLessonSecondClick() = viewModelScope.launch {
        _onNavigateToRegisterLessonSecondClick.emit(Unit)
    }

    fun navigateToRegisterLessonCheckClick() = viewModelScope.launch {
        _onNavigateToRegisterLessonCheckClick.emit(Unit)
    }

    fun registerLessonStartDateClick() = viewModelScope.launch {
        _onRegisterLessonStartDateClick.emit(startDate.value)
    }

    fun registerLessonEndDate() = viewModelScope.launch {
        _registerLessonEndDate.emit(endDate.value)
    }

    companion object {
        private const val KEY_LESSON_NAME = "lessonName"
        private const val KEY_LESSON_TOTAL_NUMBER = "lessonCount"
        private const val KEY_LESSON_CATEGORY_NAME = "lessonCategoryName"
        private const val KEY_LESSON_CATEGORY_ID = "lessonCategoryId"
        private const val KEY_LESSON_CATEGORY_SELECTED_ITEM_POSITION = "lessonCategorySelectedItemPosition"
        private const val KEY_LESSON_SITE_NAME = "lessonSiteName"
        private const val KEY_LESSON_SITE_ID = "lessonSiteId"
        private const val KEY_LESSON_SITE_SELECTED_ITEM_POSITION = "lessonSiteSelectedItemPosition"
        private const val KEY_START_DATE = "startDate"
        private const val KEY_END_DATE = "endDate"
        private const val KEY_LESSON_START_DATE = "lessonStartDate"
        private const val KEY_LESSON_END_DATE = "lessonEndDate"
        private const val KEY_LESSON_END_DATE_SELECTED_ITEM_POSITION = "lessonEndDateSelectedItemPosition"
        private const val KEY_LESSON_PRICE = "lessonPrice"
        private  const val KEY_LESSON_MESSAGE = "lessonMessage"

        const val DAY = 86400000L
        const val ONE_WEEK = 1
        const val ONE_MONTH = 2
        const val TWO_MONTH = 3
        const val THREE_MONTH = 4
        const val CUSTOM_SETTING = 5
    }
}