package com.depromeet.sloth.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
    private val memberRepository: MemberRepository
) : ViewModel() {

    open lateinit var lessonCategoryMap: HashMap<Int, String>
    open lateinit var lessonCategoryList: MutableList<String>

    open lateinit var lessonSiteMap: HashMap<Int, String>
    open lateinit var lessonSiteList: MutableList<String>

    fun removeAuthToken() = viewModelScope.launch {
        memberRepository.removeAuthToken()
    }
}