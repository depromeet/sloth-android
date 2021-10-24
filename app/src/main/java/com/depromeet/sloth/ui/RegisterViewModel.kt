package com.depromeet.sloth.ui

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.register.RegisterRepository
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class RegisterViewModel: BaseViewModel() {
    private val registerRepository = RegisterRepository()

    fun registerNickname(nickname: String) = viewModelScope.launch {
        registerRepository.registerNickname(nickname)

    }

    fun registerLesson() = viewModelScope.launch {
        registerRepository.registerLesson()
    }
}