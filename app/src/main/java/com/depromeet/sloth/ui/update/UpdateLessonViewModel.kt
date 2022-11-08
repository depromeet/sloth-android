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
import com.depromeet.sloth.ui.common.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateLessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    savedStateHandle: SavedStateHandle,
    private val stringResourcesProvider: StringResourcesProvider,
    memberRepository: MemberRepository,
) : BaseViewModel(memberRepository) {

    val lessonDetail: LessonDetail = checkNotNull(savedStateHandle[KEY_LESSON_DETAIL])

    private val _updateLessonState = MutableSharedFlow<Result<LessonUpdateResponse>>()
    val updateLessonState: SharedFlow<Result<LessonUpdateResponse>>
        get() = _updateLessonState

    private val _lessonCategoryListState =
        MutableStateFlow<Result<List<LessonCategory>>>(Result.Loading)
    val lessonCategoryListState: StateFlow<Result<List<LessonCategory>>> =
        _lessonCategoryListState.asStateFlow()

    private val _lessonSiteListState =
        MutableStateFlow<Result<List<LessonSite>>>(Result.Loading)
    val lessonSiteListState: StateFlow<Result<List<LessonSite>>> =
        _lessonSiteListState.asStateFlow()

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

    init {
        viewModelScope.launch {
            // 두 api를 병렬적으로 호출
            val lessonCategoryListResponse = async {
                _lessonCategoryListState.value = Result.Loading
                lessonRepository.fetchLessonCategoryList()
            }
            val lessonSiteListResponse = async {
                _lessonSiteListState.value = Result.Loading
                lessonRepository.fetchLessonSiteList()
            }
            _lessonCategoryListState.value = lessonCategoryListResponse.await()
            _lessonSiteListState.value = lessonSiteListResponse.await()
        }
    }

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
        _updateLessonState.emit(Result.Loading)
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

//    fun updateLesson() = viewModelScope.launch {
//        lessonRepository.updateLesson(
//            lessonDetail.lessonId.toString(),
//            LessonUpdateRequest(
//                lessonName = lessonName.value,
//                price = lessonPrice.value,
//                categoryId = lessonCategoryId.value,
//                siteId = lessonSiteId.value,
//                totalNumber = lessonTotalNumber.value,
//            )
//        ).onEach {
//            if (it is UiState.Loading) _updateLessonState.emit(UiState.Loading)
//            else _updateLessonState.emit(UiState.UnLoading)
//        }.collect {
//            _updateLessonState.emit(it)
//        }
//    }


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

    fun setLessonCategoryInfo(data: List<LessonCategory>) {
        setLessonCategoryList(data)
        setLessonCategoryId(lessonCategoryMap.value.filterValues { it == lessonDetail.categoryName }.keys.first())
        setLessonCategorySelectedItemPosition(lessonCategoryList.value.indexOf(lessonCategoryMap.value[lessonCategoryId.value]))
    }

    private fun setLessonCategoryList(data: List<LessonCategory>) {
        _lessonCategoryMap.value = data.map { it.categoryId to it.categoryName }.toMap() as HashMap<Int, String>
        _lessonCategoryList.value = data.map { it.categoryName }.toMutableList().apply {
            add(0, stringResourcesProvider.getString(R.string.choose_lesson_category))
        }
    }

    fun setLessonSiteInfo(data: List<LessonSite>) {
        setLessonSiteList(data)
        setLessonSiteId(lessonSiteMap.value.filterValues { it == lessonDetail.siteName }.keys.first())
        setLessonSiteSelectedItemPosition(lessonSiteList.value.indexOf(lessonSiteMap.value[lessonSiteId.value]))
    }

    private fun setLessonSiteList(data: List<LessonSite>) {
        _lessonSiteMap.value =
            data.map { it.siteId to it.siteName }.toMap() as HashMap<Int, String>
        _lessonSiteList.value = data.map { it.siteName }.toMutableList().apply {
            add(0, stringResourcesProvider.getString(R.string.choose_lesosn_site))
        }
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