package com.depromeet.sloth.presentation.screen.todaylesson

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonTodayResponse
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.usecase.lesson.FetchTodayLessonListUseCase
import com.depromeet.sloth.domain.usecase.lesson.UpdateLessonCountUseCase
import com.depromeet.sloth.domain.usecase.login.FetchLoginStatusUseCase
import com.depromeet.sloth.domain.usecase.member.FetchOnBoardingStatusUseCase
import com.depromeet.sloth.presentation.screen.base.BaseViewModel
import com.depromeet.sloth.util.INTERNET_CONNECTION_ERROR
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TodayLessonViewModel @Inject constructor(
    private val fetchLoginStatusUseCase: FetchLoginStatusUseCase,
    private val fetchOnBoardingStatusUseCase: FetchOnBoardingStatusUseCase,
    private val fetchLessonTodayListUseCase: FetchTodayLessonListUseCase,
    private val updateLessonCountUseCase: UpdateLessonCountUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
) : BaseViewModel() {

    private val _autoLoginEvent = MutableSharedFlow<Boolean>(replay = 1)
    val autoLoginEvent: SharedFlow<Boolean> = _autoLoginEvent.asSharedFlow()

    private val _checkOnBoardingCompleteEvent = MutableSharedFlow<Boolean>(1)
    val checkOnBoardingCompleteEvent: SharedFlow<Boolean> = _checkOnBoardingCompleteEvent.asSharedFlow()

    private val _showOnBoardingClickPlusEvent = MutableSharedFlow<Unit>()
    val showOnBoardingClickPlusEvent: SharedFlow<Unit> =
        _showOnBoardingClickPlusEvent.asSharedFlow()

    private val _showOnBoardingRegisterLessonEvent = MutableSharedFlow<Unit>()
    val showOnBoardingRegisterLessonEvent: SharedFlow<Unit> =
        _showOnBoardingRegisterLessonEvent.asSharedFlow()

    private val _onBoardingList = MutableStateFlow<List<LessonTodayResponse>>(
        mutableListOf(
            LessonTodayResponse(
                "튜토리얼",
                0,
                "나공이와 대결하기\n: 게이지를 빨리 채워 완료해봐요!",
                0,
                1,
                "",
                false,
                3,
                3
            )
        )
    )
    val onBoardingList = _onBoardingList.asStateFlow()

    init {
        checkLoginStatus()
    }

    private val _fetchLessonListSuccessEvent = MutableSharedFlow<List<LessonTodayResponse>>()
    val fetchLessonListSuccessEvent: SharedFlow<List<LessonTodayResponse>> =
        _fetchLessonListSuccessEvent.asSharedFlow()

    private val _navigateToRegisterLessonEvent = MutableSharedFlow<Unit>()
    val navigateRegisterLessonEvent: SharedFlow<Unit> =
        _navigateToRegisterLessonEvent.asSharedFlow()

    private val _navigateToWaitDialogEvent = MutableSharedFlow<Unit>()
    val navigateToWaitDialogEvent: SharedFlow<Unit> = _navigateToWaitDialogEvent.asSharedFlow()

    private fun checkLoginStatus() = viewModelScope.launch {
        _autoLoginEvent.emit(fetchLoginStatusUseCase())
    }

    fun checkOnBoardingComplete() = viewModelScope.launch {
        _checkOnBoardingCompleteEvent.emit(fetchOnBoardingStatusUseCase())
    }

    fun updateOnBoardingItemCount() {
        _onBoardingList.value = _onBoardingList.value.mapIndexed { index, item ->
            if (index == 0) item.copy(presentNumber = item.presentNumber.plus(1)) else item
        }
        Timber.d("${_onBoardingList.value[0].presentNumber}")
    }

    fun showOnBoardingClickPlus() = viewModelScope.launch {
        _showOnBoardingClickPlusEvent.emit(Unit)
    }

    fun showOnBoardingLessonRegister() = viewModelScope.launch {
        _showOnBoardingRegisterLessonEvent.emit(Unit)
    }

    fun fetchTodayLessonList() = viewModelScope.launch {
        fetchLessonTodayListUseCase()
            .onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        setInternetError(false)
                        _fetchLessonListSuccessEvent.emit(result.data)
                    }
                    is Result.Error -> {
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            setInternetError(true)
                        } else if (result.statusCode == UNAUTHORIZED) {
                            navigateToExpireDialog()
                        } else {
                            showToast(stringResourcesProvider.getString(R.string.lesson_fetch_fail))
                        }
                    }
                }
            }
    }

    //TODO 다른 함수들과 같은 형태로 변경
    suspend fun updateLessonCount(count: Int, lessonId: Int) =
        withContext(viewModelScope.coroutineContext) {
            updateLessonCountUseCase(count = count, lessonId = lessonId)
        }

    fun navigateToRegisterLesson() = viewModelScope.launch {
        _navigateToRegisterLessonEvent.emit(Unit)
    }

    fun navigateToWaitDialog() = viewModelScope.launch {
        _navigateToWaitDialogEvent.emit(Unit)
    }

    override fun retry() {
        fetchTodayLessonList()
    }
}
