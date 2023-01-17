package com.depromeet.sloth.presentation.screen.todaylesson

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.R
import com.depromeet.sloth.di.StringResourcesProvider
import com.depromeet.sloth.domain.usecase.lesson.FinishLessonUseCase
import com.depromeet.sloth.presentation.screen.base.BaseViewModel
import com.depromeet.sloth.util.INTERNET_CONNECTION_ERROR
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinishLessonViewModel @Inject constructor(
    private val finishLessonUseCase: FinishLessonUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val lessonId: String = checkNotNull(savedStateHandle[KEY_LESSON_ID])

    private val _finishLessonSuccessEvent = MutableSharedFlow<Unit>()
    val finishLessonSuccessEvent: SharedFlow<Unit> = _finishLessonSuccessEvent.asSharedFlow()

    private val _finishLessonCancelEvent = MutableSharedFlow<Unit>()
    val finishLessonCancelEvent: SharedFlow<Unit> = _finishLessonCancelEvent.asSharedFlow()

    fun finishLesson(lessonId: String) = viewModelScope.launch {
        finishLessonUseCase(lessonId)
            .onEach { result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        showToast(stringResourcesProvider.getString(R.string.lesson_finish_complete))
                        _finishLessonSuccessEvent.emit(Unit)
                    }

                    is Result.Error -> {
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            showToast(stringResourcesProvider.getString(R.string.lesson_finish_fail_by_internet_error))
                        } else if (result.statusCode == UNAUTHORIZED) {
                            navigateToExpireDialog()
                        } else {
                            showToast(stringResourcesProvider.getString(R.string.lesson_finish_fail))
                        }
                    }
                }
            }
    }

    fun finishLessonCancel() = viewModelScope.launch {
        _finishLessonCancelEvent.emit(Unit)
    }

    companion object {
        private const val KEY_LESSON_ID = "lesson_id"
    }

    override fun retry() = Unit
}