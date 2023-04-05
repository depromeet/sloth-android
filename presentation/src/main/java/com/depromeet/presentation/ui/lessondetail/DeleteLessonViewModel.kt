package com.depromeet.presentation.ui.lessondetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.domain.usecase.lesson.DeleteLessonUseCase
import com.depromeet.domain.util.Result
import com.depromeet.presentation.R
import com.depromeet.presentation.di.StringResourcesProvider
import com.depromeet.presentation.ui.base.BaseViewModel
import com.depromeet.presentation.util.INTERNET_CONNECTION_ERROR
import com.depromeet.presentation.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DeleteLessonViewModel @Inject constructor(
    private val deleteLessonUseCase: DeleteLessonUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val lessonId: String = checkNotNull(savedStateHandle[KEY_LESSON_ID])

    private val _deleteLessonSuccessEvent = MutableSharedFlow<Unit>()
    val deleteLessonSuccessEvent: SharedFlow<Unit> = _deleteLessonSuccessEvent.asSharedFlow()

    private val _deleteLessonCancelEvent = MutableSharedFlow<Unit>()
    val deleteLessonCancelEvent: SharedFlow<Unit> = _deleteLessonCancelEvent.asSharedFlow()

    fun deleteLesson() = viewModelScope.launch {
        deleteLessonUseCase(lessonId)
            .onEach { result ->
                setLoading(result is Result.Loading)
            }
            .collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        showToast(stringResourcesProvider.getString(R.string.lesson_delete_complete))
                        _deleteLessonSuccessEvent.emit(Unit)
                    }
                    is Result.Error -> {
                        when {
                            result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                showToast(stringResourcesProvider.getString(R.string.lesson_delete_fail_by_internet))
                            }
                            result.statusCode == UNAUTHORIZED -> {
                                navigateToExpireDialog()
                            }
                            else -> showToast(stringResourcesProvider.getString(R.string.lesson_delete_fail))
                        }
                    }
                }
            }
    }

    fun deleteLessonCancel() = viewModelScope.launch {
        _deleteLessonCancelEvent.emit(Unit)
    }

    companion object {
        private const val KEY_LESSON_ID = "lesson_id"
    }

    override fun retry() = Unit
}