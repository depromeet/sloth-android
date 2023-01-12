package com.depromeet.sloth.presentation.screen.registerlesson

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.request.lesson.LessonRegisterRequest
import com.depromeet.sloth.data.model.response.lesson.LessonCategoryResponse
import com.depromeet.sloth.data.model.response.lesson.LessonSiteResponse
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.use_case.lesson.FetchLessonCategoryListUseCase
import com.depromeet.sloth.domain.use_case.lesson.FetchLessonSiteListUseCase
import com.depromeet.sloth.domain.use_case.lesson.RegisterLessonUseCase
import com.depromeet.sloth.extensions.getMutableStateFlow
import com.depromeet.sloth.extensions.getPickerDateToDash
import com.depromeet.sloth.presentation.screen.base.BaseViewModel
import com.depromeet.sloth.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RegisterLessonViewModel @Inject constructor(
    private val registerLessonUseCase: RegisterLessonUseCase,
    private val fetchLessonCategoryListUseCase: FetchLessonCategoryListUseCase,
    private val fetchLessonSiteListUseCase: FetchLessonSiteListUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    private val _registerLessonSuccessEvent = MutableSharedFlow<Unit>()
    val registerLessonSuccessEvent: SharedFlow<Unit> = _registerLessonSuccessEvent.asSharedFlow()

    private val _fetchLessonCategoryListSuccessEvent =
        MutableSharedFlow<List<LessonCategoryResponse>>()
    val fetchLessonCategoryListSuccessEvent: SharedFlow<List<LessonCategoryResponse>> =
        _fetchLessonCategoryListSuccessEvent.asSharedFlow()

    private val _fetchLessonSiteListSuccessEvent = MutableSharedFlow<List<LessonSiteResponse>>()
    val fetchLessonSiteListSuccessEvent: SharedFlow<List<LessonSiteResponse>> =
        _fetchLessonSiteListSuccessEvent.asSharedFlow()

    private val _navigateToRegisterLessonSecondEvent = MutableSharedFlow<Unit>()
    val navigateToRegisterLessonSecondEvent: SharedFlow<Unit> =
        _navigateToRegisterLessonSecondEvent.asSharedFlow()

    private val _navigateToRegisterLessonCheckEvent = MutableSharedFlow<Unit>()
    val navigateToRegisterLessonCheckEvent: SharedFlow<Unit> =
        _navigateToRegisterLessonCheckEvent.asSharedFlow()

    private val _registerLessonStartDateEvent = MutableSharedFlow<Date>()
    val registerLessonStartDateEvent: SharedFlow<Date> =
        _registerLessonStartDateEvent.asSharedFlow()

    private val _registerLessonEndDateEvent = MutableSharedFlow<Date>()
    val registerLessonEndDateEvent: SharedFlow<Date> = _registerLessonEndDateEvent.asSharedFlow()

    private val _startDate = savedStateHandle.getMutableStateFlow(KEY_START_DATE, Date())
    val startDate: StateFlow<Date> = _startDate.asStateFlow()

    private val _lessonStartDate =
        savedStateHandle.getMutableStateFlow(KEY_LESSON_START_DATE, DEFAULT_STRING_VALUE)
    val lessonStartDate: StateFlow<String> = _lessonStartDate.asStateFlow()

    init {
        initLessonStartDate()
    }

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

    private val _endDate = savedStateHandle.getMutableStateFlow(KEY_END_DATE, Date())
    val endDate: StateFlow<Date> = _endDate.asStateFlow()

    private val _lessonEndDate =
        savedStateHandle.getMutableStateFlow(KEY_LESSON_END_DATE, DEFAULT_STRING_VALUE)
    val lessonEndDate: StateFlow<String> = _lessonEndDate.asStateFlow()

    private val _lessonEndDateSelectedItemPosition = savedStateHandle.getMutableStateFlow(
        KEY_LESSON_END_DATE_SELECTED_ITEM_POSITION, 0
    )
    val lessonEndDateSelectedItemPosition: StateFlow<Int> =
        _lessonEndDateSelectedItemPosition.asStateFlow()

    private val _lessonEndDateSelectEvent = MutableStateFlow(false)
    private val lessonEndDateSelectEvent: StateFlow<Boolean> =
        _lessonEndDateSelectEvent.asStateFlow()

    private val _lessonCategoryMap = MutableStateFlow<HashMap<Int, String>>(hashMapOf())
    val lessonCategoryMap: StateFlow<HashMap<Int, String>> = _lessonCategoryMap.asStateFlow()

    private val _lessonSiteMap = MutableStateFlow<HashMap<Int, String>>(hashMapOf())
    val lessonSiteMap: StateFlow<HashMap<Int, String>> = _lessonSiteMap.asStateFlow()

    private val _lessonCategoryList = MutableStateFlow<List<String>>(mutableListOf())
    val lessonCategoryList: StateFlow<List<String>> = _lessonCategoryList.asStateFlow()

    private val _lessonSiteList = MutableStateFlow<List<String>>(mutableListOf())
    val lessonSiteList: StateFlow<List<String>> = _lessonSiteList.asStateFlow()

    private val _lessonDateRangeValidation = MutableStateFlow(true)
    val lessonDateRangeValidation: StateFlow<Boolean> = _lessonDateRangeValidation.asStateFlow()

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
        lessonEndDateSelectEvent,
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

    private fun setLessonCategoryList(data: List<LessonCategoryResponse>) {
        _lessonCategoryMap.value =
            data.map { it.categoryId to it.categoryName }.toMap() as HashMap<Int, String>
        _lessonCategoryList.value = data.map { it.categoryName }.toMutableList().apply {
            add(0, stringResourcesProvider.getString(R.string.choose_lesson_category))
        }
    }

    private fun setLessonSiteList(data: List<LessonSiteResponse>) {
        _lessonSiteMap.value =
            data.map { it.siteId to it.siteName }.toMap() as HashMap<Int, String>
        _lessonSiteList.value = data.map { it.siteName }.toMutableList().apply {
            add(0, stringResourcesProvider.getString(R.string.choose_lesosn_site))
        }
    }

    private fun setLessonDateRangeValidation() {
        _lessonDateRangeValidation.value = startDate.value <= endDate.value
    }

    fun registerLesson() = viewModelScope.launch {
        registerLessonUseCase(
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
        ).onEach { result ->
            setLoading(result is Result.Loading)
        }.collect { result ->
            when (result) {
                is Result.Loading -> return@collect
                is Result.Success -> {
                    showToast(stringResourcesProvider.getString(R.string.lesson_register_complete))
                    _registerLessonSuccessEvent.emit(Unit)
                }
                is Result.Error -> {
                    if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                        showToast(stringResourcesProvider.getString(R.string.lesson_register_fail_by_internet_error))
                    } else if (result.statusCode == UNAUTHORIZED) {
                        navigateToExpireDialog()
                    } else {
                        showToast(stringResourcesProvider.getString(R.string.lesson_finish_fail))
                    }
                }
            }
        }
    }

    fun fetchLessonCategoryList() = viewModelScope.launch {
        fetchLessonCategoryListUseCase()
            .onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        setLessonCategoryList(result.data)
                        _fetchLessonCategoryListSuccessEvent.emit(result.data)
                        setInternetError(false)
                    }

                    is Result.Error -> {
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            setInternetError(true)
                        } else if (result.statusCode == UNAUTHORIZED) {
                            navigateToExpireDialog()
                        } else {
                            showToast(stringResourcesProvider.getString(R.string.lesson_category_fetch_fail))
                        }
                    }
                }
            }
    }

    fun fetchLessonSiteList() = viewModelScope.launch {
        fetchLessonSiteListUseCase()
            .onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        setInternetError(false)
                        setLessonSiteList(result.data)
                        _fetchLessonSiteListSuccessEvent.emit(result.data)
                    }

                    is Result.Error -> {
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            setInternetError(true)
                        } else if (result.statusCode == UNAUTHORIZED) {
                            navigateToExpireDialog()
                        } else {
                            showToast(stringResourcesProvider.getString(R.string.lesson_site_fetch_fail))
                        }
                    }
                }
            }
    }

    fun setLessonCategorySelectedItemPosition(position: Int) {
        _lessonCategorySelectedItemPosition.value = position
    }

    fun setLessonSiteItemPosition(position: Int) {
        _lessonSiteSelectedItemPosition.value = position
    }

    fun setLessonEndDateSelectedItemPosition(position: Int) {
        _lessonEndDateSelectedItemPosition.value = position
        _lessonEndDateSelectEvent.value = lessonEndDateSelectedItemPosition.value != 0
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

    fun navigateToRegisterLessonSecond() = viewModelScope.launch {
        _navigateToRegisterLessonSecondEvent.emit(Unit)
    }

    fun navigateToRegisterLessonCheck() = viewModelScope.launch {
        _navigateToRegisterLessonCheckEvent.emit(Unit)
    }

    fun registerLessonStartDate() = viewModelScope.launch {
        _registerLessonStartDateEvent.emit(startDate.value)
    }

    fun registerLessonEndDate() = viewModelScope.launch {
        _registerLessonEndDateEvent.emit(endDate.value)
    }

    override fun retry() {
        fetchLessonCategoryList()
        fetchLessonSiteList()
    }

    companion object {
        private const val KEY_LESSON_NAME = "lessonName"
        private const val KEY_LESSON_TOTAL_NUMBER = "lessonCount"
        private const val KEY_LESSON_CATEGORY_NAME = "lessonCategoryName"
        private const val KEY_LESSON_CATEGORY_ID = "lessonCategoryId"
        private const val KEY_LESSON_CATEGORY_SELECTED_ITEM_POSITION =
            "lessonCategorySelectedItemPosition"
        private const val KEY_LESSON_SITE_NAME = "lessonSiteName"
        private const val KEY_LESSON_SITE_ID = "lessonSiteId"
        private const val KEY_LESSON_SITE_SELECTED_ITEM_POSITION = "lessonSiteSelectedItemPosition"
        private const val KEY_START_DATE = "startDate"
        private const val KEY_END_DATE = "endDate"
        private const val KEY_LESSON_START_DATE = "lessonStartDate"
        private const val KEY_LESSON_END_DATE = "lessonEndDate"
        private const val KEY_LESSON_END_DATE_SELECTED_ITEM_POSITION =
            "lessonEndDateSelectedItemPosition"
        private const val KEY_LESSON_PRICE = "lessonPrice"
        private const val KEY_LESSON_MESSAGE = "lessonMessage"

        const val DAY = 60L * 60L * 24L
        const val ONE_WEEK = 1
        const val ONE_MONTH = 2
        const val TWO_MONTH = 3
        const val THREE_MONTH = 4
        const val CUSTOM_SETTING = 5
    }
}