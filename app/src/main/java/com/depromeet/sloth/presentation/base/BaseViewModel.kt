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

    private val _navigateToExpireDialogEvent = MutableSharedFlow<Unit>()
    val navigateToExpireDialogEvent: SharedFlow<Unit> = _navigateToExpireDialogEvent.asSharedFlow()

    private val _showToastEvent = MutableSharedFlow<String>()
    val showToastEvent: SharedFlow<String> = _showToastEvent.asSharedFlow()


    abstract fun retry()

    suspend fun setLoading(isLoading: Boolean) {
        _isLoading.emit(isLoading)
    }

    suspend fun setInternetError(error: Boolean) {
        _internetError.emit(error)
    }

    suspend fun navigateToExpireDialog() {
        _navigateToExpireDialogEvent.emit(Unit)
    }

    suspend fun showToast(message: String) {
        _showToastEvent.emit(message)
    }
}