package com.depromeet.presentation.ui.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.domain.usecase.member.DeleteAuthTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpiredViewModel @Inject constructor(
    private val deleteAuthTokenUseCase: DeleteAuthTokenUseCase
): ViewModel() {

    private val _navigateToLoginEvent = MutableSharedFlow<Unit>()
    val navigateToLoginEvent: SharedFlow<Unit> = _navigateToLoginEvent.asSharedFlow()

    fun navigateToLogin() = viewModelScope.launch {
        deleteAuthToken()
        _navigateToLoginEvent.emit(Unit)
    }

    private fun deleteAuthToken() = viewModelScope.launch {
        deleteAuthTokenUseCase()
    }
}