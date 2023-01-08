package com.depromeet.sloth.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel: ViewModel() {

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    abstract fun retry()

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}