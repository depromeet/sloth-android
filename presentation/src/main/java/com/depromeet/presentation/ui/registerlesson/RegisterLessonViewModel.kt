package com.depromeet.presentation.ui.registerlesson

import android.annotation.SuppressLint
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.domain.usecase.lesson.FetchLessonCategoryListUseCase
import com.depromeet.domain.usecase.lesson.FetchLessonSiteListUseCase
import com.depromeet.domain.usecase.lesson.RegisterLessonUseCase
import com.depromeet.domain.util.Result
import com.depromeet.presentation.R
import com.depromeet.presentation.di.StringResourcesProvider
import com.depromeet.presentation.extensions.getMutableStateFlow
import com.depromeet.presentation.mapper.toEntity
import com.depromeet.presentation.mapper.toUiModel
import com.depromeet.presentation.model.LessonCategory
import com.depromeet.presentation.model.LessonRegisterRequest
import com.depromeet.presentation.model.LessonSite
import com.depromeet.presentation.ui.base.BaseViewModel
import com.depromeet.presentation.util.CALENDAR_TIME_ZONE
import com.depromeet.presentation.util.DEFAULT_STRING_VALUE
import com.depromeet.presentation.util.INTERNET_CONNECTION_ERROR
import com.depromeet.presentation.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
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

    private var fetchLessonCategoryListJob: Job? = null
    private var fetchLessonSiteListJob: Job? = null

    private val fragmentId: Int = checkNotNull(savedStateHandle[KEY_FRAGMENT_ID])

    private val _registerLessonSuccessEvent = MutableSharedFlow<Int>()
    val registerLessonSuccessEvent: SharedFlow<Int> = _registerLessonSuccessEvent.asSharedFlow()

    private val _fetchLessonCategoryListSuccessEvent =
        MutableSharedFlow<List<LessonCategory>>()
    val fetchLessonCategoryListSuccessEvent: SharedFlow<List<LessonCategory>> =
        _fetchLessonCategoryListSuccessEvent.asSharedFlow()

    private val _fetchLessonSiteListSuccessEvent = MutableSharedFlow<List<LessonSite>>()
    val fetchLessonSiteListSuccessEvent: SharedFlow<List<LessonSite>> =
        _fetchLessonSiteListSuccessEvent.asSharedFlow()

    private val _navigateToRegisterLessonSecondEvent = MutableSharedFlow<Unit>()
    val navigateToRegisterLessonSecondEvent: SharedFlow<Unit> =
        _navigateToRegisterLessonSecondEvent.asSharedFlow()

    private val _navigateToRegisterLessonCheckEvent = MutableSharedFlow<Unit>()
    val navigateToRegisterLessonCheckEvent: SharedFlow<Unit> =
        _navigateToRegisterLessonCheckEvent.asSharedFlow()

    private val _registerLessonStartDateEvent = MutableSharedFlow<Unit>()
    val registerLessonStartDateEvent: SharedFlow<Unit> = _registerLessonStartDateEvent.asSharedFlow()

    private val _registerLessonEndDateEvent = MutableSharedFlow<Unit>()
    val registerLessonEndDateEvent: SharedFlow<Unit> = _registerLessonEndDateEvent.asSharedFlow()

    @SuppressLint("NewApi")
    private val _lessonStartDate = savedStateHandle.getMutableStateFlow(KEY_LESSON_START_DATE, ZonedDateTime.now(ZoneId.of(CALENDAR_TIME_ZONE)))
    val lessonStartDate: StateFlow<ZonedDateTime> = _lessonStartDate.asStateFlow()

    private val _lessonName = savedStateHandle.getMutableStateFlow(KEY_LESSON_NAME, DEFAULT_STRING_VALUE)
    val lessonName: StateFlow<String> = _lessonName.asStateFlow()

    private val _lessonTotalNumber = savedStateHandle.getMutableStateFlow(KEY_LESSON_TOTAL_NUMBER, 0)
    val lessonTotalNumber: StateFlow<Int> = _lessonTotalNumber.asStateFlow()

    private var lessonCategoryId = 0
    private var lessonSiteId = 0

    private val _lessonCategoryName = savedStateHandle.getMutableStateFlow(KEY_LESSON_CATEGORY_NAME, DEFAULT_STRING_VALUE)
    val lessonCategoryName: StateFlow<String> = _lessonCategoryName.asStateFlow()

    private val _lessonCategorySelectedItemPosition = savedStateHandle.getMutableStateFlow(
        KEY_LESSON_CATEGORY_SELECTED_ITEM_POSITION, 0
    )
    val lessonCategorySelectedItemPosition: StateFlow<Int> = _lessonCategorySelectedItemPosition.asStateFlow()

    private val _lessonSiteName = savedStateHandle.getMutableStateFlow(KEY_LESSON_SITE_NAME, DEFAULT_STRING_VALUE)
    val lessonSiteName: StateFlow<String> = _lessonSiteName.asStateFlow()

    private val _lessonSiteSelectedItemPosition = savedStateHandle.getMutableStateFlow(
        KEY_LESSON_SITE_SELECTED_ITEM_POSITION, 0
    )
    val lessonSiteSelectedItemPosition: StateFlow<Int> = _lessonSiteSelectedItemPosition.asStateFlow()

    private val _lessonPrice = savedStateHandle.getMutableStateFlow(KEY_LESSON_PRICE, 0)
    val lessonPrice: StateFlow<Int> = _lessonPrice.asStateFlow()

    private val _lessonMessage = savedStateHandle.getMutableStateFlow(KEY_LESSON_MESSAGE, DEFAULT_STRING_VALUE)
    val lessonMessage: StateFlow<String> = _lessonMessage.asStateFlow()

    @SuppressLint("NewApi")
    private val _lessonEndDate = savedStateHandle.getMutableStateFlow(KEY_LESSON_END_DATE, ZonedDateTime.now(ZoneId.of(CALENDAR_TIME_ZONE)))
    val lessonEndDate: StateFlow<ZonedDateTime> = _lessonEndDate.asStateFlow()

    private val _lessonEndDateSelectedItemPosition = savedStateHandle.getMutableStateFlow(
        KEY_LESSON_END_DATE_SELECTED_ITEM_POSITION, 0
    )
    val lessonEndDateSelectedItemPosition: StateFlow<Int> =
        _lessonEndDateSelectedItemPosition.asStateFlow()

    private val _lessonEndDateSelectEvent = MutableStateFlow(false)
    private val lessonEndDateSelectEvent: StateFlow<Boolean> =
        _lessonEndDateSelectEvent.asStateFlow()

    private var lessonCategoryMap = hashMapOf<Int, String>()
    private var lessonSiteMap = hashMapOf<Int, String>()

    private val _lessonCategoryList = MutableStateFlow<List<String>>(mutableListOf())
    val lessonCategoryList: StateFlow<List<String>> = _lessonCategoryList.asStateFlow()

//    var lessonCategoryList = mutableListOf<String>()
//    var lessonSiteList = mutableListOf<String>()

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

    @SuppressLint("NewApi")
    fun setLessonStartDate(startDate: ZonedDateTime) {
        _lessonStartDate.value = startDate
        setLessonDateRangeValidation()
    }

    @SuppressLint("NewApi")
    fun setLessonEndDateBySpinner(position: Int?) {
        if (position == CUSTOM_SETTING) return

        val startDate = lessonStartDate.value
        when (position) {
            ONE_WEEK -> _lessonEndDate.value = startDate.plusDays(7)
            ONE_MONTH -> _lessonEndDate.value = startDate.plusMonths(1)
            TWO_MONTH -> _lessonEndDate.value = startDate.plusMonths(2)
            THREE_MONTH -> _lessonEndDate.value = startDate.plusMonths(3)
        }
        setLessonDateRangeValidation()
    }

    fun setLessonEndDateByCalendar(endDate: ZonedDateTime) {
        _lessonEndDate.value = endDate
        setLessonDateRangeValidation()
    }

    private fun initLessonCategoryList(data: List<LessonCategory>) {
        lessonCategoryMap = data.associate { it.categoryId to it.categoryName } as HashMap<Int, String>
        _lessonCategoryList.value = data.map { it.categoryName }.toMutableList().apply {
            add(0, stringResourcesProvider.getString(R.string.choose_lesson_category))
        }
//        lessonCategoryList = data.map { it.categoryName }.toMutableList().apply {
//            add(0, stringResourcesProvider.getString(R.string.choose_lesson_category))
//        }
    }

    private fun initLessonSiteList(data: List<LessonSite>) {
        lessonSiteMap = data.associate { it.siteId to it.siteName } as HashMap<Int, String>
        _lessonSiteList.value = data.map { it.siteName }.toMutableList().apply {
            add(0, stringResourcesProvider.getString(R.string.choose_lesson_site))
        }
//        lessonSiteList = data.map { it.siteName }.toMutableList().apply {
//            add(0, stringResourcesProvider.getString(R.string.choose_lesson_site))
//        }
    }

    @SuppressLint("NewApi")
    private fun setLessonDateRangeValidation() {
        _lessonDateRangeValidation.value = lessonStartDate.value <= lessonEndDate.value
    }

    @SuppressLint("NewApi")
    fun registerLesson() = viewModelScope.launch {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        registerLessonUseCase(
            LessonRegisterRequest(
                alertDays = null,
                categoryId = lessonCategoryId,
                endDate = lessonEndDate.value.format(formatter),
                lessonName = lessonName.value,
                message = lessonMessage.value,
                price = lessonPrice.value,
                siteId = lessonSiteId,
                startDate = lessonStartDate.value.format(formatter),
                totalNumber = lessonTotalNumber.value
            ).toEntity()
        ).onEach { result ->
            setLoading(result is Result.Loading)
        }.collect { result ->
            when (result) {
                is Result.Loading -> return@collect
                is Result.Success -> {
                    showToast(stringResourcesProvider.getString(R.string.lesson_register_complete))
                    _registerLessonSuccessEvent.emit(fragmentId)
                }
                is Result.Error -> {
                    when {
                        result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                            showToast(stringResourcesProvider.getString(R.string.lesson_register_fail_by_internet_error))
                        }
                        result.statusCode == UNAUTHORIZED -> {
                            navigateToExpireDialog()
                        }
                        else -> showToast(stringResourcesProvider.getString(R.string.lesson_finish_fail))
                    }
                }
            }
        }
    }

    fun fetchLessonCategoryList() {
        if (fetchLessonCategoryListJob != null) return

        fetchLessonCategoryListJob = viewModelScope.launch {
            fetchLessonCategoryListUseCase()
                .onEach { result ->
                    setLoading(result is Result.Loading)
                }.collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> {
                            initLessonCategoryList(result.data.toUiModel())
                            _fetchLessonCategoryListSuccessEvent.emit(result.data.toUiModel())
                            setInternetError(false)
                        }
                        is Result.Error -> {
                            when {
                                result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                    setInternetError(true)
                                }
                                result.statusCode == UNAUTHORIZED -> {
                                    navigateToExpireDialog()
                                }
                                else -> showToast(stringResourcesProvider.getString(R.string.lesson_category_fetch_fail))
                            }
                        }
                    }
                    fetchLessonCategoryListJob = null
                }
        }
    }

    fun fetchLessonSiteList() {
        if (fetchLessonSiteListJob != null) return

        fetchLessonSiteListJob = viewModelScope.launch {
            fetchLessonSiteListUseCase()
                .onEach { result ->
                    setLoading(result is Result.Loading)
                }.collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> {
                            setInternetError(false)
                            initLessonSiteList(result.data.toUiModel())
                            _fetchLessonSiteListSuccessEvent.emit(result.data.toUiModel())
                        }
                        is Result.Error -> {
                            when {
                                result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                    setInternetError(true)
                                }
                                result.statusCode == UNAUTHORIZED -> {
                                    navigateToExpireDialog()
                                }
                                else -> showToast(stringResourcesProvider.getString(R.string.lesson_site_fetch_fail))
                            }
                        }
                    }
                    fetchLessonSiteListJob = null
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

    fun setLessonCategoryId(lessonCategory: String) {
        lessonCategoryId = lessonCategoryMap.filterValues { it == lessonCategory }.keys.first()
    }

    fun setLessonCategoryName(lessonCategoryName: String) {
        _lessonCategoryName.value = lessonCategoryName
    }

    fun setLessonSiteId(lessonSite: String) {
        lessonSiteId = lessonSiteMap.filterValues { it == lessonSite }.keys.first()
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
        _registerLessonStartDateEvent.emit(Unit)
    }

    fun registerLessonEndDate() = viewModelScope.launch {
        _registerLessonEndDateEvent.emit(Unit)
    }

    override fun retry() {
        fetchLessonCategoryList()
        fetchLessonSiteList()
    }

    companion object {
        private const val KEY_FRAGMENT_ID = "fragment_id"

        private const val KEY_LESSON_NAME = "lessonName"
        private const val KEY_LESSON_TOTAL_NUMBER = "lessonCount"
        private const val KEY_LESSON_CATEGORY_NAME = "lessonCategoryName"
        private const val KEY_LESSON_CATEGORY_SELECTED_ITEM_POSITION = "lessonCategorySelectedItemPosition"
        private const val KEY_LESSON_SITE_NAME = "lessonSiteName"
        private const val KEY_LESSON_SITE_SELECTED_ITEM_POSITION = "lessonSiteSelectedItemPosition"
        private const val KEY_LESSON_START_DATE = "startDate"
        private const val KEY_LESSON_END_DATE = "endDate"
        private const val KEY_LESSON_END_DATE_SELECTED_ITEM_POSITION = "lessonEndDateSelectedItemPosition"
        private const val KEY_LESSON_PRICE = "lessonPrice"
        private const val KEY_LESSON_MESSAGE = "lessonMessage"

        const val ONE_WEEK = 1
        const val ONE_MONTH = 2
        const val TWO_MONTH = 3
        const val THREE_MONTH = 4
        const val CUSTOM_SETTING = 5
    }
}