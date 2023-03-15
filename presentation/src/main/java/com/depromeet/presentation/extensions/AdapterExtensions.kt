package com.depromeet.presentation.extensions

import androidx.databinding.ViewDataBinding


fun <T : ViewDataBinding> T.executeAfter(block: T.() -> Unit) {
    block()
    executePendingBindings()
}