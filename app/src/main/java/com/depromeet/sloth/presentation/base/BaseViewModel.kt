package com.depromeet.sloth.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel: ViewModel() {

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _internetError = MutableStateFlow(false)
    val internetError: StateFlow<Boolean> = _internetError.asStateFlow()

    private val _showForbiddenDialogEvent = MutableSharedFlow<Unit>()
    val showForbiddenDialogEvent: SharedFlow<Unit> = _showForbiddenDialogEvent.asSharedFlow()

    private val _showToastEvent = MutableSharedFlow<String>()
    val showToastEvent: SharedFlow<String> = _showToastEvent.asSharedFlow()


    abstract fun retry()

    suspend fun showLoading(isLoading: Boolean) {
        _isLoading.emit(isLoading)
    }

    suspend fun internetError(error: Boolean) {
        _internetError.emit(error)
    }

    suspend fun showForbiddenDialogEvent() {
        _showForbiddenDialogEvent.emit(Unit)
    }

    suspend fun showToastEvent(message: String) {
        _showToastEvent.emit(message)
    }
}