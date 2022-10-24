package com.depromeet.sloth.ui.update

import androidx.lifecycle.*
import com.depromeet.sloth.data.model.LessonCategory
import com.depromeet.sloth.data.model.LessonDetail
import com.depromeet.sloth.data.model.LessonSite
import com.depromeet.sloth.data.model.LessonUpdate
import com.depromeet.sloth.data.network.lesson.LessonRepository
import com.depromeet.sloth.data.network.lesson.LessonState
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateRequest
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.extensions.addSourceList
import com.depromeet.sloth.ui.base.BaseViewModel
import com.depromeet.sloth.ui.common.Event
import com.depromeet.sloth.ui.register.RegisterLessonViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateLessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    memberRepository: MemberRepository,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel(memberRepository) {

    val lessonDetail: LessonDetail = savedStateHandle.get(KEY_LESSON_DETAIL)
        ?: throw IllegalStateException("There is no value of the lesson id.")

    private val _lessonUpdateState = MutableLiveData<Event<LessonState<LessonUpdate>>>()
    val lessonUpdateState: LiveData<Event<LessonState<LessonUpdate>>>
        get() = _lessonUpdateState

    private val _lessonName =
        savedStateHandle.getLiveData<String>(RegisterLessonViewModel.KEY_LESSON_NAME, "")
    val lessonName: LiveData<String>
        get() = _lessonName

    private val _lessonTotalNumber =
        savedStateHandle.getLiveData<Int>(RegisterLessonViewModel.KEY_LESSON_TOTAL_NUMBER, 0)
    val lessonTotalNumber: LiveData<Int>
        get() = _lessonTotalNumber

    private val _lessonPrice =
        savedStateHandle.getLiveData<Int>(RegisterLessonViewModel.KEY_LESSON_PRICE, 0)
    val lessonPrice: LiveData<Int>
        get() = _lessonPrice

    private val _lessonCategoryListState =
        MutableLiveData<LessonState<List<LessonCategory>>>()
    val lessonCategoryListState: LiveData<LessonState<List<LessonCategory>>> =
        _lessonCategoryListState

    private val _lessonSiteListState = MutableLiveData<LessonState<List<LessonSite>>>()
    val lessonSiteListState: LiveData<LessonState<List<LessonSite>>>
        get() = _lessonSiteListState

    private val _lessonCategoryMap = savedStateHandle.getLiveData<HashMap<Int, String>>(
        RegisterLessonViewModel.KEY_LESSON_CATEGORY_MAP, HashMap<Int, String>()
    )
    val lessonCategoryMap: LiveData<HashMap<Int, String>>
        get() = _lessonCategoryMap

    private val _lessonCategoryList = savedStateHandle.getLiveData<MutableList<String>>(
        RegisterLessonViewModel.KEY_LESSON_CATEGORY_LIST, mutableListOf()
    )
    val lessonCategoryList: LiveData<MutableList<String>>
        get() = _lessonCategoryList

    private val _lessonCategoryId =
        savedStateHandle.getLiveData<Int>(RegisterLessonViewModel.KEY_LESSON_CATEGORY_ID, 0)
    val lessonCategoryId: LiveData<Int>
        get() = _lessonCategoryId

    private val _lessonCategoryName =
        savedStateHandle.getLiveData<String>(RegisterLessonViewModel.KEY_LESSON_CATEGORY_NAME, "")
    val lessonCategoryName: LiveData<String>
        get() = _lessonCategoryName

    private val _lessonCategorySelectedItemPosition = savedStateHandle.getLiveData<Int>(
        RegisterLessonViewModel.KEY_LESSON_CATEGORY_SELECTED_ITEM_POSITION, 0
    )
    val lessonCategorySelectedItemPosition: LiveData<Int>
        get() = _lessonCategorySelectedItemPosition

    private val _lessonSiteMap = savedStateHandle.getLiveData<HashMap<Int, String>>(
        RegisterLessonViewModel.KEY_LESSON_SITE_MAP, HashMap<Int, String>()
    )
    val lessonSiteMap: LiveData<HashMap<Int, String>>
        get() = _lessonSiteMap

    private val _lessonSiteList = savedStateHandle.getLiveData<MutableList<String>>(
        RegisterLessonViewModel.KEY_LESSON_SITE_LIST, mutableListOf()
    )
    val lessonSiteList: LiveData<MutableList<String>>
        get() = _lessonSiteList

    private val _lessonSiteId =
        savedStateHandle.getLiveData<Int>(RegisterLessonViewModel.KEY_LESSON_SITE_ID, 0)
    val lessonSiteId: LiveData<Int>
        get() = _lessonSiteId

    private val _lessonSiteName =
        savedStateHandle.getLiveData<String>(RegisterLessonViewModel.KEY_LESSON_SITE_NAME, "")
    val lessonSiteName: LiveData<String>
        get() = _lessonSiteName

    private val _lessonSiteSelectedItemPosition = savedStateHandle.getLiveData<Int>(
        RegisterLessonViewModel.KEY_LESSON_SITE_SELECTED_ITEM_POSITION, 0
    )
    val lessonSiteSelectedItemPosition: LiveData<Int>
        get() = _lessonSiteSelectedItemPosition

    init {
        viewModelScope.launch {
            fetchLessonCategoryList()
            fetchLessonSiteList()
        }
    }

    private val _lessonNumberValidation = MutableLiveData<Boolean>()
    val lessonNumberValidation: LiveData<Boolean>
        get() = _lessonNumberValidation

    fun setLessonName(lessonName: String?) {
        if (this.lessonName.value == lessonName || lessonName == null) {
            return
        }
        savedStateHandle.set(RegisterLessonViewModel.KEY_LESSON_NAME, lessonName)
    }

    fun setLessonPrice(lessonPrice: Int?) {
        if (this.lessonPrice.value == lessonPrice || lessonPrice == null) {
            return
        }

        savedStateHandle.set(RegisterLessonViewModel.KEY_LESSON_PRICE, lessonPrice)
    }

    fun setLessonTotalNumber(lessonTotalNumber: Int?) {
        if (this.lessonTotalNumber.value == lessonTotalNumber || lessonTotalNumber == null) {
            return
        }
        savedStateHandle.set(RegisterLessonViewModel.KEY_LESSON_TOTAL_NUMBER, lessonTotalNumber)
    }

    fun updateLesson() = viewModelScope.launch {
        if (lessonTotalNumber.value!! < lessonDetail.presentNumber) {
            _lessonNumberValidation.value = false
        } else {
            _lessonNumberValidation.value = true
            _lessonUpdateState.value = Event(LessonState.Loading)
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
            _lessonUpdateState.value = Event(lessonUpdateResponse)
        }
    }

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
        savedStateHandle.set(RegisterLessonViewModel.KEY_LESSON_CATEGORY_ID, lessonCategoryId)
    }

    fun setCategoryName(lessonCategoryName: String?) {
        if (this.lessonCategoryName.value == lessonCategoryName || lessonCategoryName == null) {
            return
        }

        savedStateHandle.set(RegisterLessonViewModel.KEY_LESSON_CATEGORY_NAME, lessonCategoryName)
    }

    fun setSiteId(lessonSiteId: Int?) {
        if (this.lessonSiteId.value == lessonSiteId || lessonSiteId == null) {
            return
        }

        savedStateHandle.set(RegisterLessonViewModel.KEY_LESSON_SITE_ID, lessonSiteId)
    }

    fun setSiteName(lessonSiteName: String?) {
        if (this.lessonSiteName.value == lessonSiteName || lessonSiteName == null) {
            return
        }

        savedStateHandle.set(RegisterLessonViewModel.KEY_LESSON_SITE_NAME, lessonSiteName)
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
            add(0, PLEASE_CHOOSE_LESSON_CATEGORY)
        }
    }

    fun setLessonSiteList(data: List<LessonSite>) {
        _lessonSiteMap.value =
                //data.map { it.siteId to it.siteName }.toMap() as HashMap<Int, String>
            data.associate { it.siteId to it.siteName } as HashMap<Int, String>
        _lessonSiteList.value = data.map { it.siteName }.toMutableList().apply {
            add(0, PLEASE_CHOOSE_LESSON_SITE)
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
        const val PLEASE_CHOOSE_LESSON_CATEGORY = "강의 사이트를 선택해 주세요"
        const val PLEASE_CHOOSE_LESSON_SITE = "강의 사이트를 선택해 주세요"
    }
}