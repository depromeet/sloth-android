package com.depromeet.sloth.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    //공통적으로 호출했었을 때 이 함수를 호출하여 데이터를 가공
    open fun fetchData(): Job = viewModelScope.launch{ }

}