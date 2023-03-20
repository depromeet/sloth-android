package com.depromeet.presentation.ui.updatelesson

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.domain.usecase.lesson.FetchLessonCategoryListUseCase
import com.depromeet.domain.usecase.lesson.FetchLessonSiteListUseCase
import com.depromeet.domain.usecase.lesson.UpdateLessonUseCase
import com.depromeet.domain.util.Result
import com.depromeet.presentation.R
import com.depromeet.presentation.di.StringResourcesProvider
import com.depromeet.presentation.extensions.getMutableStateFlow
import com.depromeet.presentation.mapper.toEntity
import com.depromeet.presentation.mapper.toUiModel
import com.depromeet.presentation.model.LessonCategory
import com.depromeet.presentation.model.LessonDetail
import com.depromeet.presentation.model.LessonSite
import com.depromeet.presentation.model.LessonUpdateRequest
import com.depromeet.presentation.ui.base.BaseViewModel
import com.depromeet.presentation.util.INTERNET_CONNECTION_ERROR
import com.depromeet.presentation.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UpdateLessonViewModel @Inject constructor(
    private val updateLessonUseCase: UpdateLessonUseCase,
    private val fetchLessonCategoryListUseCase: FetchLessonCategoryListUseCase,
    private val fetchLessonSiteListUseCase: FetchLessonSiteListUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    val lessonDetail: LessonDetail = checkNotNull(savedStateHandle[KEY_LESSON_DETAIL])

    private val _updateLessonSuccessEvent = MutableSharedFlow<Unit>()
    val updateLessonSuccessEvent: SharedFlow<Unit> = _updateLessonSuccessEvent.asSharedFlow()

    private val _fetchLessonCategoryListSuccessEvent =
        MutableSharedFlow<List<LessonCategory>>()
    val fetchLessonCategoryListSuccessEvent: SharedFlow<List<LessonCategory>> =
        _fetchLessonCategoryListSuccessEvent.asSharedFlow()

    private val _fetchLessonSiteListSuccessEvent = MutableSharedFlow<List<LessonSite>>()
    val fetchLessonSiteListSuccessEvent: SharedFlow<List<LessonSite>> =
        _fetchLessonSiteListSuccessEvent.asSharedFlow()

    init {
        fetchLessonCategoryList()
        fetchLessonSiteList()
    }

    private val _lessonName = savedStateHandle.getMutableStateFlow(KEY_LESSON_NAME, lessonDetail.lessonName)
    val lessonName: StateFlow<String> = _lessonName.asStateFlow()

    private val _lessonTotalNumber = savedStateHandle.getMutableStateFlow(KEY_LESSON_TOTAL_NUMBER, lessonDetail.totalNumber)
    val lessonTotalNumber: StateFlow<Int> = _lessonTotalNumber.asStateFlow()

    private val _lessonPrice = savedStateHandle.getMutableStateFlow(KEY_LESSON_PRICE, lessonDetail.price)
    val lessonPrice: StateFlow<Int> = _lessonPrice.asStateFlow()

    private var lessonCategoryMap = hashMapOf<Int, String>()
    private var lessonSiteMap = hashMapOf<Int, String>()

    private val _lessonCategoryList = MutableStateFlow<List<String>>(mutableListOf())
    val lessonCategoryList: StateFlow<List<String>> = _lessonCategoryList.asStateFlow()

    private var lessonCategoryId = 0
    private var lessonSiteId = 0

    private val _lessonCategorySelectedItemPosition = savedStateHandle.getMutableStateFlow(
        KEY_LESSON_CATEGORY_SELECTED_ITEM_POSITION, 0
    )

    val lessonCategorySelectedItemPosition: StateFlow<Int> =
        _lessonCategorySelectedItemPosition.asStateFlow()

    private val _lessonSiteList = MutableStateFlow<List<String>>(mutableListOf())
    val lessonSiteList: StateFlow<List<String>> = _lessonSiteList.asStateFlow()

    private val _lessonSiteSelectedItemPosition = savedStateHandle.getMutableStateFlow(
        KEY_LESSON_SITE_SELECTED_ITEM_POSITION, 0
    )
    val lessonSiteSelectedItemPosition: StateFlow<Int> =
        _lessonSiteSelectedItemPosition.asStateFlow()

    private val _lessonTotalNumberValidation = MutableStateFlow(true)
    val lessonTotalNumberValidation: StateFlow<Boolean> = _lessonTotalNumberValidation.asStateFlow()

    fun setLessonName(lessonName: String) {
        _lessonName.value = lessonName
    }

    fun setLessonPrice(lessonPrice: Int) {
        _lessonPrice.value = lessonPrice
    }

    fun setLessonTotalNumber(lessonTotalNumber: Int) {
        _lessonTotalNumber.value = lessonTotalNumber
        setLessonTotalNumberValidation()
    }

    fun updateLesson() = viewModelScope.launch {
        updateLessonUseCase(
            lessonDetail.lessonId,
            LessonUpdateRequest(
                lessonName = lessonName.value,
                price = lessonPrice.value,
                categoryId = lessonCategoryId,
                siteId = lessonSiteId,
                totalNumber = lessonTotalNumber.value,
            ).toEntity()
        ).onEach { result ->
            setLoading(result is Result.Loading)
        }.collect { result ->
            when (result) {
                is Result.Loading -> return@collect
                is Result.Success -> {
                    showToast(stringResourcesProvider.getString(R.string.lesson_update_complete))
                    _updateLessonSuccessEvent.emit(Unit)
                }
                is Result.Error -> {
                    when {
                        result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                            showToast(stringResourcesProvider.getString(R.string.lesson_update_fail_by_internet_error))
                        }
                        result.statusCode == UNAUTHORIZED -> {
                            navigateToExpireDialog()
                        }
                        else -> showToast(stringResourcesProvider.getString(R.string.lesson_update_fail))
                    }
                }
            }
        }
    }

    private fun fetchLessonCategoryList() = viewModelScope.launch {
        fetchLessonCategoryListUseCase()
            .onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        setInternetError(false)
                        setLessonCategoryInfo(result.data.toUiModel())
                        _fetchLessonCategoryListSuccessEvent.emit(result.data.toUiModel())
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
            }
    }

    private fun fetchLessonSiteList() = viewModelScope.launch {
        fetchLessonSiteListUseCase()
            .onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        setInternetError(false)
                        setLessonSiteInfo(result.data.toUiModel())
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
            }
    }

    fun setLessonCategoryId(lessonCategory: String) {
        lessonCategoryId = lessonCategoryMap.filterValues { it == lessonCategory }.keys.first()
    }

    fun setLessonSiteId(lessonSite: String) {
        lessonSiteId = lessonSiteMap.filterValues { it == lessonSite }.keys.first()
    }

    fun setLessonCategorySelectedItemPosition(position: Int) {
        _lessonCategorySelectedItemPosition.value = position
    }

    fun setLessonSiteSelectedItemPosition(position: Int) {
        _lessonSiteSelectedItemPosition.value = position
    }

    private fun setLessonTotalNumberValidation() {
        _lessonTotalNumberValidation.value = lessonTotalNumber.value >= lessonDetail.presentNumber
    }

    // 초기값이 false 인데 왜 버튼이 활성화 되어있지 -> 값을 바로 방출하니까 true 로
    val updateLessonButtonState = combine(
        lessonName,
        lessonCategorySelectedItemPosition,
        lessonSiteSelectedItemPosition,
        lessonTotalNumberValidation
    ) { name, categorySelectedItemPosition, siteSelectedItemPosition, totalNumberValidation ->
        name.isNotBlank() && categorySelectedItemPosition != 0
                && siteSelectedItemPosition != 0 && totalNumberValidation
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    private fun setLessonCategoryInfo(data: List<LessonCategory>) {
        setLessonCategoryList(data)
        setLessonCategoryId(lessonDetail.categoryName)
        setLessonCategorySelectedItemPosition(lessonCategoryList.value.indexOf(lessonCategoryMap[lessonCategoryId]))
    }

    private fun setLessonCategoryList(data: List<LessonCategory>) {
        lessonCategoryMap = data.associate { it.categoryId to it.categoryName } as HashMap<Int, String>
        _lessonCategoryList.value = data.map { it.categoryName }.toMutableList().apply {
            add(0, stringResourcesProvider.getString(R.string.choose_lesson_category))
        }
    }

    private fun setLessonSiteInfo(data: List<LessonSite>) {
        setLessonSiteList(data)
        setLessonSiteId(lessonDetail.siteName)
        setLessonSiteSelectedItemPosition(lessonSiteList.value.indexOf(lessonSiteMap[lessonSiteId]))
    }

    private fun setLessonSiteList(data: List<LessonSite>) {
        lessonSiteMap = data.associate { it.siteId to it.siteName } as HashMap<Int, String>
        _lessonSiteList.value = data.map { it.siteName }.toMutableList().apply {
            add(0, stringResourcesProvider.getString(R.string.choose_lesson_site))
        }
    }

    override fun retry() {
        fetchLessonCategoryList()
        fetchLessonSiteList()
    }

    companion object {
        private const val KEY_LESSON_DETAIL = "lesson_detail"

        private const val KEY_LESSON_NAME = "lessonName"
        private const val KEY_LESSON_TOTAL_NUMBER = "lessonCount"
        private const val KEY_LESSON_CATEGORY_SELECTED_ITEM_POSITION = "lessonCategorySelectedItemPosition"
        private const val KEY_LESSON_SITE_SELECTED_ITEM_POSITION = "lessonSiteSelectedItemPosition"
        private const val KEY_LESSON_PRICE = "lessonPrice"
    }
}