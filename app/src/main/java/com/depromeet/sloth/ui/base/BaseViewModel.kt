package com.depromeet.sloth.ui.base

import androidx.lifecycle.ViewModel

abstract class BaseViewModel: ViewModel() {

    abstract fun retry()
}