package com.depromeet.sloth.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.member.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
    private val memberRepository: MemberRepository
) : ViewModel() {

    fun removeAuthToken() = viewModelScope.launch {
        memberRepository.removeAuthToken()
    }
}