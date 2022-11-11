package com.depromeet.sloth.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
    private val memberRepository: MemberRepository
) : ViewModel() {

    //TODO error 이벤트 처리용
    private val _errorToast = MutableSharedFlow<String>()
    val errorToast: SharedFlow<String> = _errorToast.asSharedFlow()

    fun removeAuthToken() = viewModelScope.launch {
        memberRepository.removeAuthToken()
    }
}