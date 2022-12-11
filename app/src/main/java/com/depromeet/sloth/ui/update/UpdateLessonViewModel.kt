package com.depromeet.sloth.ui.update

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.common.Result
import com.depromeet.sloth.data.model.request.lesson.LessonUpdateRequest
import com.depromeet.sloth.data.model.response.lesson.LessonCategoryResponse
import com.depromeet.sloth.data.model.response.lesson.LessonDetailResponse
import com.depromeet.sloth.data.model.response.lesson.LessonSiteResponse
import com.depromeet.sloth.data.model.response.lesson.LessonUpdateResponse
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.use_case.lesson.GetLessonCategoryListUseCase
import com.depromeet.sloth.domain.use_case.lesson.GetLessonSiteListUseCase
import com.depromeet.sloth.domain.use_case.lesson.UpdateLessonUseCase
import com.depromeet.sloth.domain.use_case.member.RemoveAuthTokenUseCase
import com.depromeet.sloth.extensions.getMutableStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateLessonViewModel @Inject constructor(
    private val updateLessonUseCase: UpdateLessonUseCase,
    getLessonCategoryListUseCase: GetLessonCategoryListUseCase,
    getLessonSiteListUseCase: GetLessonSiteListUseCase,
    private val removeAuthTokenUseCase: RemoveAuthTokenUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val lessonDetail: LessonDetailResponse = checkNotNull(savedStateHandle[KEY_LESSON_DETAIL])

    private val _updateLessonState = MutableSharedFlow<Result<LessonUpdateResponse>>()
    val updateLessonState: SharedFlow<Result<LessonUpdateResponse>>
        get() = _updateLessonState

    val lessonCategoryListState: Flow<Result<List<LessonCategoryResponse>>> =
        getLessonCategoryListUseCase()

    val lessonSiteListState: Flow<Result<List<LessonSiteResponse>>> =
        getLessonSiteListUseCase()

    // helper class 를 만들어 기존의 형태에 맞춰 값을 set 할 수 있게 변경
    private val _lessonName =
        savedStateHandle.getMutableStateFlow(KEY_LESSON_NAME, lessonDetail.lessonName)
    val lessonName: StateFlow<String> = _lessonName.asStateFlow()

    private val _lessonTotalNumber =
        savedStateHandle.getMutableStateFlow(KEY_LESSON_TOTAL_NUMBER, lessonDetail.totalNumber)
    val lessonTotalNumber: StateFlow<Int> = _lessonTotalNumber.asStateFlow()

    private val _lessonPrice = savedStateHandle.getMutableStateFlow(KEY_LESSON_PRICE, lessonDetail.price)
    val lessonPrice: StateFlow<Int> = _lessonPrice.asStateFlow()

    // TODO 굳이 stateflow 로 관리할 이유가
    private val _lessonCategoryMap = MutableStateFlow<HashMap<Int, String>>(hashMapOf())
    val lessonCategoryMap: StateFlow<HashMap<Int, String>> = _lessonCategoryMap.asStateFlow()

    private val _lessonCategoryList = MutableStateFlow<List<String>>(mutableListOf())
    val lessonCategoryList: StateFlow<List<String>> = _lessonCategoryList.asStateFlow()

    private val _lessonCategoryId = savedStateHandle.getMutableStateFlow(KEY_LESSON_CATEGORY_ID, 0)
    val lessonCategoryId: StateFlow<Int> = _lessonCategoryId.asStateFlow()

    private val _lessonCategorySelectedItemPosition = savedStateHandle.getMutableStateFlow(
        KEY_LESSON_CATEGORY_SELECTED_ITEM_POSITION, 0
    )

    private val _lessonSiteMap = MutableStateFlow<HashMap<Int, String>>(hashMapOf())
    val lessonSiteMap: StateFlow<HashMap<Int, String>> = _lessonSiteMap.asStateFlow()

    private val _lessonSiteList = MutableStateFlow<List<String>>(mutableListOf())
    val lessonSiteList: StateFlow<List<String>> = _lessonSiteList.asStateFlow()

    val lessonCategorySelectedItemPosition: StateFlow<Int> =
        _lessonCategorySelectedItemPosition.asStateFlow()

    private val _lessonSiteId = savedStateHandle.getMutableStateFlow(KEY_LESSON_SITE_ID, 0)
    val lessonSiteId: StateFlow<Int> = _lessonSiteId.asStateFlow()

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
            lessonDetail.lessonId.toString(),
            LessonUpdateRequest(
                lessonName = lessonName.value,
                price = lessonPrice.value,
                categoryId = lessonCategoryId.value,
                siteId = lessonSiteId.value,
                totalNumber = lessonTotalNumber.value,
            )
        ).onEach {
            if (it is Result.Loading) _updateLessonState.emit(Result.Loading)
            else _updateLessonState.emit(Result.UnLoading)
        }.collect {
            _updateLessonState.emit(it)
        }
    }

    fun setLessonCategoryId(lessonCategoryId: Int) {
        _lessonCategoryId.value = lessonCategoryId
    }

    fun setLessonSiteId(lessonSiteId: Int) {
        _lessonSiteId.value = lessonSiteId
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
        name.isNotBlank()  && categorySelectedItemPosition != 0
                && siteSelectedItemPosition != 0 && totalNumberValidation
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    fun setLessonCategoryInfo(data: List<LessonCategoryResponse>) {
        setLessonCategoryList(data)
        setLessonCategoryId(lessonCategoryMap.value.filterValues { it == lessonDetail.categoryName }.keys.first())
        setLessonCategorySelectedItemPosition(lessonCategoryList.value.indexOf(lessonCategoryMap.value[lessonCategoryId.value]))
    }

    private fun setLessonCategoryList(data: List<LessonCategoryResponse>) {
        _lessonCategoryMap.value = data.map { it.categoryId to it.categoryName }.toMap() as HashMap<Int, String>
        _lessonCategoryList.value = data.map { it.categoryName }.toMutableList().apply {
            add(0, stringResourcesProvider.getString(R.string.choose_lesson_category))
        }
    }

    fun setLessonSiteInfo(data: List<LessonSiteResponse>) {
        setLessonSiteList(data)
        setLessonSiteId(lessonSiteMap.value.filterValues { it == lessonDetail.siteName }.keys.first())
        setLessonSiteSelectedItemPosition(lessonSiteList.value.indexOf(lessonSiteMap.value[lessonSiteId.value]))
    }

    private fun setLessonSiteList(data: List<LessonSiteResponse>) {
        _lessonSiteMap.value =
            data.map { it.siteId to it.siteName }.toMap() as HashMap<Int, String>
        _lessonSiteList.value = data.map { it.siteName }.toMutableList().apply {
            add(0, stringResourcesProvider.getString(R.string.choose_lesosn_site))
        }
    }

    fun removeAuthToken() = viewModelScope.launch {
        removeAuthTokenUseCase()
    }

    companion object {
        private const val KEY_LESSON_DETAIL = "lessonDetail"

        private const val KEY_LESSON_NAME = "lessonName"
        private const val KEY_LESSON_TOTAL_NUMBER = "lessonCount"
        private const val KEY_LESSON_CATEGORY_ID = "lessonCategoryId"
        private const val KEY_LESSON_CATEGORY_SELECTED_ITEM_POSITION =
            "lessonCategorySelectedItemPosition"
        private const val KEY_LESSON_SITE_ID = "lessonSiteId"
        private const val KEY_LESSON_SITE_SELECTED_ITEM_POSITION = "lessonSiteSelectedItemPosition"
        private const val KEY_LESSON_PRICE = "lessonPrice"
    }
}