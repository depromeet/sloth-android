package com.depromeet.sloth.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
    private val memberRepository: MemberRepository
) : ViewModel() {

    open fun fetchData(): Job = viewModelScope.launch { }

    fun removeAuthToken() = viewModelScope.launch {
        memberRepository.removeAuthToken()
    }
}