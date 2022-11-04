package com.depromeet.sloth.ui.update

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.LessonDetail
import com.depromeet.sloth.data.network.lesson.LessonCategory
import com.depromeet.sloth.data.network.lesson.LessonSite
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateRequest
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateResponse
import com.depromeet.sloth.data.repository.LessonRepository
import com.depromeet.sloth.data.repository.MemberRepository
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.extensions.getMutableStateFlow
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateLessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    private val savedStateHandle: SavedStateHandle,
    private val stringResourcesProvider: StringResourcesProvider,
    memberRepository: MemberRepository,
) : BaseViewModel(memberRepository) {

    val lessonDetail: LessonDetail = checkNotNull(savedStateHandle[KEY_LESSON_DETAIL])

    private val _updateLessonState = MutableSharedFlow<UiState<LessonUpdateResponse>>()
    val updateLessonState: SharedFlow<UiState<LessonUpdateResponse>>
        get() = _updateLessonState

    private val _lessonCategoryListState =
        MutableStateFlow<UiState<List<LessonCategory>>>(UiState.Loading)
    val lessonCategoryListState: StateFlow<UiState<List<LessonCategory>>> =
        _lessonCategoryListState.asStateFlow()

    private val _lessonSiteListState =
        MutableStateFlow<UiState<List<LessonSite>>>(UiState.Loading)
    val lessonSiteListState: StateFlow<UiState<List<LessonSite>>> =
        _lessonSiteListState.asStateFlow()

    private val _lessonName =
        savedStateHandle.getMutableStateFlow(KEY_LESSON_NAME, lessonDetail.lessonName)
    val lessonName: StateFlow<String> = _lessonName.asStateFlow()

    private val _lessonTotalNumber =
        savedStateHandle.getMutableStateFlow(KEY_LESSON_TOTAL_NUMBER, lessonDetail.totalNumber)
    val lessonTotalNumber: StateFlow<Int> = _lessonTotalNumber.asStateFlow()

    private val _lessonPrice = savedStateHandle.getMutableStateFlow(KEY_LESSON_PRICE, lessonDetail.price)
    private val lessonPrice: StateFlow<Int> = _lessonPrice.asStateFlow()

    private val _lessonCategoryMap = MutableStateFlow<HashMap<Int, String>>(hashMapOf())
    val lessonCategoryMap: StateFlow<HashMap<Int, String>> = _lessonCategoryMap.asStateFlow()

    private val _lessonSiteMap = MutableStateFlow<HashMap<Int, String>>(hashMapOf())
    val lessonSiteMap: StateFlow<HashMap<Int, String>> = _lessonSiteMap.asStateFlow()

    private val _lessonCategoryList = MutableStateFlow<List<String>>(mutableListOf())
    val lessonCategoryList: StateFlow<List<String>> = _lessonCategoryList.asStateFlow()

    private val _lessonSiteList = MutableStateFlow<List<String>>(mutableListOf())
    val lessonSiteList: StateFlow<List<String>> = _lessonSiteList.asStateFlow()

    // TODO 굳이 stateflow 로 관리할 이유가
    private val _lessonCategoryId = savedStateHandle.getMutableStateFlow(KEY_LESSON_CATEGORY_ID, 0)
    val lessonCategoryId: StateFlow<Int> = _lessonCategoryId.asStateFlow()

    private val _lessonCategoryName =
        savedStateHandle.getMutableStateFlow(KEY_LESSON_CATEGORY_NAME, lessonDetail.categoryName)
    val lessonCategoryName: StateFlow<String> = _lessonCategoryName.asStateFlow()

    private val _lessonCategorySelectedItemPosition = savedStateHandle.getMutableStateFlow(
        KEY_LESSON_CATEGORY_SELECTED_ITEM_POSITION, 0
    )
    val lessonCategorySelectedItemPosition: StateFlow<Int> =
        _lessonCategorySelectedItemPosition.asStateFlow()

    private val _lessonSiteId = savedStateHandle.getMutableStateFlow(KEY_LESSON_SITE_ID, 0)
    val lessonSiteId: StateFlow<Int> = _lessonSiteId.asStateFlow()

    private val _lessonSiteName =
        savedStateHandle.getMutableStateFlow(KEY_LESSON_SITE_NAME, lessonDetail.siteName)
    val lessonSiteName: StateFlow<String> = _lessonSiteName.asStateFlow()

    private val _lessonSiteSelectedItemPosition = savedStateHandle.getMutableStateFlow(
        KEY_LESSON_SITE_SELECTED_ITEM_POSITION, 0
    )
    val lessonSiteSelectedItemPosition: StateFlow<Int> =
        _lessonSiteSelectedItemPosition.asStateFlow()

    private val _lessonTotalNumberValidation = MutableStateFlow(true)
    val lessonTotalNumberValidation: StateFlow<Boolean> = _lessonTotalNumberValidation.asStateFlow()

    init {
        viewModelScope.launch {
            // 두 api를 병렬적으로 호출
            val lessonCategoryListResponse = async {
                _lessonCategoryListState.value = UiState.Loading
                lessonRepository.fetchLessonCategoryList()
            }
            val lessonSiteListResponse = async {
                _lessonSiteListState.value = UiState.Loading
                lessonRepository.fetchLessonSiteList()
            }
            _lessonCategoryListState.value = lessonCategoryListResponse.await()
            _lessonSiteListState.value = lessonSiteListResponse.await()
        }
    }

    fun setLessonName(lessonName: String?) {
        if (this.lessonName.value == lessonName || lessonName == null) {
            return
        }
        savedStateHandle[KEY_LESSON_NAME] = lessonName
    }

    fun setLessonPrice(lessonPrice: Int?) {
        if (this.lessonPrice.value == lessonPrice || lessonPrice == null) {
            return
        }
        savedStateHandle[KEY_LESSON_PRICE] = lessonPrice
    }

    fun setLessonTotalNumber(lessonTotalNumber: Int?) {
        if (this.lessonTotalNumber.value == lessonTotalNumber || lessonTotalNumber == null) {
            return
        }
        savedStateHandle[KEY_LESSON_TOTAL_NUMBER] = lessonTotalNumber
        setLessonTotalNumberValidation()
    }

    fun updateLesson() = viewModelScope.launch {
        _lessonTotalNumberValidation.value = true
        _updateLessonState.emit(UiState.Loading)
        _updateLessonState.emit(
            lessonRepository.updateLesson(
                lessonDetail.lessonId.toString(),
                LessonUpdateRequest(
                    lessonName = lessonName.value,
                    price = lessonPrice.value,
                    categoryId = lessonCategoryId.value,
                    siteId = lessonSiteId.value,
                    totalNumber = lessonTotalNumber.value,
                )
            )
        )
    }

    fun setLessonCategoryId(lessonCategoryId: Int?) {
        if (this.lessonCategoryId.value == lessonCategoryId || lessonCategoryId == null) {
            return
        }
        savedStateHandle[KEY_LESSON_CATEGORY_ID] = lessonCategoryId
    }

    fun setLessonCategoryName(lessonCategoryName: String?) {
        if (this.lessonCategoryName.value == lessonCategoryName || lessonCategoryName == null) {
            return
        }
        savedStateHandle[KEY_LESSON_CATEGORY_NAME] = lessonCategoryName
    }

    fun setLessonSiteId(lessonSiteId: Int?) {
        if (this.lessonSiteId.value == lessonSiteId || lessonSiteId == null) {
            return
        }
        savedStateHandle[KEY_LESSON_SITE_ID] = lessonSiteId
    }

    fun setLessonSiteName(lessonSiteName: String?) {
        if (this.lessonSiteName.value == lessonSiteName || lessonSiteName == null) {
            return
        }
        savedStateHandle[KEY_LESSON_SITE_NAME] = lessonSiteName
    }

    fun setLessonCategoryItemPosition(position: Int?) {
        if (this.lessonCategorySelectedItemPosition.value == position || position == null) {
            return
        }
        savedStateHandle[KEY_LESSON_CATEGORY_SELECTED_ITEM_POSITION] = position
    }

    fun setLessonSiteItemPosition(position: Int?) {
        if (this.lessonSiteSelectedItemPosition.value == position || position == null) {
            return
        }
        savedStateHandle[KEY_LESSON_SITE_SELECTED_ITEM_POSITION] = position
    }

    private fun setLessonTotalNumberValidation() {
        _lessonTotalNumberValidation.value = lessonTotalNumber.value >= lessonDetail.presentNumber
    }

    val updateLessonButtonState = combine(
        lessonName,
        lessonTotalNumber,
        lessonCategorySelectedItemPosition,
        lessonSiteSelectedItemPosition,
        lessonTotalNumberValidation
    ) { lessonName, lessonTotalNumber, lessonCategorySelectedItemPosition, lessonSiteSelectedItemPosition, lessonTotalNumberValidation ->
        lessonName.isNotBlank() && lessonTotalNumber != 0 && lessonCategorySelectedItemPosition != 0
                && lessonSiteSelectedItemPosition != 0 && lessonTotalNumberValidation
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    //TODO SaveableMutableStateFlow mapper 확인
    fun setLessonCategoryInfo(data: List<LessonCategory>) {
        setLessonCategoryList(data)
        setLessonCategoryId(lessonCategoryMap.value.filterValues { it == lessonCategoryName.value }.keys.first())
        setLessonCategoryItemPosition(lessonCategoryList.value.indexOf(lessonCategoryMap.value[lessonCategoryId.value]))
    }

    private fun setLessonCategoryList(data: List<LessonCategory>) {
        _lessonCategoryMap.value =
            data.map { it.categoryId to it.categoryName }.toMap() as HashMap<Int, String>
        _lessonCategoryList.value = data.map { it.categoryName }.toMutableList().apply {
            add(0, stringResourcesProvider.getString(R.string.choose_lesson_category))
        }
    }

    fun setLessonSiteInfo(data: List<LessonSite>) {
        setLessonSiteList(data)
        setLessonSiteId(lessonSiteMap.value.filterValues { it == lessonSiteName.value }.keys.first())
        setLessonSiteItemPosition(lessonSiteList.value.indexOf(lessonSiteMap.value[lessonSiteId.value]))
    }

    private fun setLessonSiteList(data: List<LessonSite>) {
        _lessonSiteMap.value =
            data.map { it.siteId to it.siteName }.toMap() as HashMap<Int, String>
        _lessonSiteList.value = data.map { it.siteName }.toMutableList().apply {
            add(0, stringResourcesProvider.getString(R.string.choose_lesosn_site))
        }
    }

    companion object {
        const val KEY_LESSON_DETAIL = "lessonDetail"

        const val KEY_LESSON_NAME = "lessonName"
        const val KEY_LESSON_TOTAL_NUMBER = "lessonCount"
        const val KEY_LESSON_CATEGORY_NAME = "lessonCategoryName"
        const val KEY_LESSON_CATEGORY_ID = "lessonCategoryId"
        const val KEY_LESSON_CATEGORY_SELECTED_ITEM_POSITION =
            "lessonCategorySelectedItemPosition"
        const val KEY_LESSON_SITE_NAME = "lessonSiteName"
        const val KEY_LESSON_SITE_ID = "lessonSiteId"
        const val KEY_LESSON_SITE_SELECTED_ITEM_POSITION = "lessonSiteSelectedItemPosition"
        const val KEY_LESSON_PRICE = "lessonPrice"
    }
}