package com.depromeet.sloth.ui.update

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
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
import com.depromeet.sloth.extensions.addSourceList
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//TODO ViewModel 내에서 UiState 핸들링할 수 있게 변경
//TODO !! 제거
@HiltViewModel
class UpdateLessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    memberRepository: MemberRepository,
    private val savedStateHandle: SavedStateHandle,
    private val stringResourcesProvider: StringResourcesProvider
) : BaseViewModel(memberRepository) {

    val lessonDetail: LessonDetail = checkNotNull(savedStateHandle[KEY_LESSON_DETAIL])

    private val _lessonUpdateState = MutableSharedFlow<UiState<LessonUpdateResponse>>()
    val lessonUpdateState: SharedFlow<UiState<LessonUpdateResponse>>
        get() = _lessonUpdateState

    private val _lessonName =
        savedStateHandle.getLiveData<String>(KEY_LESSON_NAME, "")
    val lessonName: LiveData<String>
        get() = _lessonName

    private val _lessonTotalNumber =
        savedStateHandle.getLiveData<Int>(KEY_LESSON_TOTAL_NUMBER, 0)
    val lessonTotalNumber: LiveData<Int>
        get() = _lessonTotalNumber

    private val _lessonPrice =
        savedStateHandle.getLiveData<Int>(KEY_LESSON_PRICE, 0)
    private val lessonPrice: LiveData<Int>
        get() = _lessonPrice

    private val _lessonCategoryListState = MutableLiveData<UiState<List<LessonCategory>>>()
    val lessonCategoryListState: LiveData<UiState<List<LessonCategory>>>
        get() = _lessonCategoryListState

    private val _lessonSiteListState = MutableLiveData<UiState<List<LessonSite>>>()
    val lessonSiteListState: LiveData<UiState<List<LessonSite>>>
        get() = _lessonSiteListState

    private val _lessonCategoryMap = savedStateHandle.getLiveData<HashMap<Int, String>>(
        KEY_LESSON_CATEGORY_MAP, HashMap<Int, String>()
    )
    val lessonCategoryMap: LiveData<HashMap<Int, String>>
        get() = _lessonCategoryMap

    private val _lessonCategoryList = savedStateHandle.getLiveData<MutableList<String>>(
        KEY_LESSON_CATEGORY_LIST, mutableListOf()
    )
    val lessonCategoryList: LiveData<MutableList<String>>
        get() = _lessonCategoryList

    private val _lessonCategoryId =
        savedStateHandle.getLiveData<Int>(KEY_LESSON_CATEGORY_ID, 0)
    private val lessonCategoryId: LiveData<Int>
        get() = _lessonCategoryId

    private val _lessonCategoryName =
        savedStateHandle.getLiveData<String>(KEY_LESSON_CATEGORY_NAME, "")
    private val lessonCategoryName: LiveData<String>
        get() = _lessonCategoryName

    private val _lessonCategorySelectedItemPosition = savedStateHandle.getLiveData<Int>(
        KEY_LESSON_CATEGORY_SELECTED_ITEM_POSITION, 0
    )
    val lessonCategorySelectedItemPosition: LiveData<Int>
        get() = _lessonCategorySelectedItemPosition

    private val _lessonSiteMap = savedStateHandle.getLiveData<HashMap<Int, String>>(
        KEY_LESSON_SITE_MAP, HashMap<Int, String>()
    )
    val lessonSiteMap: LiveData<HashMap<Int, String>>
        get() = _lessonSiteMap

    private val _lessonSiteList = savedStateHandle.getLiveData<MutableList<String>>(
        KEY_LESSON_SITE_LIST, mutableListOf()
    )
    val lessonSiteList: LiveData<MutableList<String>>
        get() = _lessonSiteList

    private val _lessonSiteId =
        savedStateHandle.getLiveData<Int>(KEY_LESSON_SITE_ID, 0)
    private val lessonSiteId: LiveData<Int>
        get() = _lessonSiteId

    private val _lessonSiteName =
        savedStateHandle.getLiveData<String>(KEY_LESSON_SITE_NAME, "")
    private val lessonSiteName: LiveData<String>
        get() = _lessonSiteName

    private val _lessonSiteSelectedItemPosition = savedStateHandle.getLiveData<Int>(
        KEY_LESSON_SITE_SELECTED_ITEM_POSITION, 0
    )
    val lessonSiteSelectedItemPosition: LiveData<Int>
        get() = _lessonSiteSelectedItemPosition

    private val _lessonNumberValidation = MutableLiveData<Boolean>()
    val lessonNumberValidation: LiveData<Boolean>
        get() = _lessonNumberValidation

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
        // setLessonUpdateInfo()
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
    }

    fun updateLesson() = viewModelScope.launch {
        if (lessonTotalNumber.value!! < lessonDetail.presentNumber) {
            _lessonNumberValidation.value = false
        } else {
            _lessonNumberValidation.value = true
            _lessonUpdateState.emit(UiState.Loading)
            val lessonUpdateResponse =
                lessonRepository.updateLesson(
                    lessonDetail.lessonId.toString(),
                    LessonUpdateRequest(
                        lessonName = lessonName.value!!,
                        price = lessonPrice.value!!,
                        categoryId = lessonCategoryId.value!!,
                        siteId = lessonSiteId.value!!,
                        totalNumber = lessonTotalNumber.value!!,
                    )
                )
            _lessonUpdateState.emit(lessonUpdateResponse)
        }
    }

//    private suspend fun fetchLessonCategoryList() {
//        _lessonCategoryListState.value = LessonState.Loading
//        _lessonCategoryListState.value = lessonRepository.fetchLessonCategoryList()
//    }
//
//    private suspend fun fetchLessonSiteList() {
//        _lessonSiteListState.value = LessonState.Loading
//        _lessonSiteListState.value = lessonRepository.fetchLessonSiteList()
//    }

    fun setLessonCategoryItemPosition(position: Int) {
        _lessonCategorySelectedItemPosition.value = position
    }

    fun setLessonSiteItemPosition(position: Int) {
        _lessonSiteSelectedItemPosition.value = position
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

    val isEnabledLessonUpdateButton = MediatorLiveData<Boolean>().apply {
        addSourceList(
            _lessonName,
            _lessonTotalNumber,
            _lessonPrice,
            _lessonCategorySelectedItemPosition,
            _lessonSiteSelectedItemPosition
        ) {
            isValidEnterInfo()
        }
    }

    private fun isValidEnterInfo() =
        !lessonName.value.isNullOrBlank() && lessonTotalNumber.value != 0 && lessonPrice.value != 0 &&
                lessonCategorySelectedItemPosition.value != 0 && lessonSiteSelectedItemPosition.value != 0

    fun setLessonCategoryList(data: List<LessonCategory>) {
        _lessonCategoryMap.value =
                //data.map { it.categoryId to it.categoryName }.toMap() as HashMap<Int, String>
            data.associate { it.categoryId to it.categoryName } as HashMap<Int, String>
        _lessonCategoryList.value = data.map { it.categoryName }.toMutableList().apply {
            add(0, stringResourcesProvider.getString(R.string.choose_lesson_category))
        }
    }

    fun setLessonSiteList(data: List<LessonSite>) {
        _lessonSiteMap.value =
                //data.map { it.siteId to it.siteName }.toMap() as HashMap<Int, String>
            data.associate { it.siteId to it.siteName } as HashMap<Int, String>
        _lessonSiteList.value = data.map { it.siteName }.toMutableList().apply {
            add(0, stringResourcesProvider.getString(R.string.choose_lesosn_site))
        }
    }

    fun setLessonUpdateInfo() = with(lessonDetail) {
        _lessonName.value = lessonName
        _lessonTotalNumber.value = totalNumber
        _lessonPrice.value = price
        _lessonCategoryId.value =
            lessonCategoryMap.value!!.filterValues { it == categoryName }.keys.first()
        _lessonSiteId.value = lessonSiteMap.value!!.filterValues { it == siteName }.keys.first()
        _lessonCategorySelectedItemPosition.value =
            lessonCategoryList.value!!.indexOf(lessonCategoryMap.value!![lessonCategoryId.value!!])
        _lessonSiteSelectedItemPosition.value =
            lessonSiteList.value!!.indexOf(lessonSiteMap.value!![lessonSiteId.value!!])
    }

    companion object {
        const val KEY_LESSON_DETAIL = "lessonDetail"

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
        const val KEY_LESSON_PRICE = "lessonPrice"
    }
}