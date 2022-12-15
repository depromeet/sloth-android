package com.depromeet.sloth.extensions

import androidx.databinding.ViewDataBinding

fun <T : ViewDataBinding> T.executeAfter(block: T.() -> Unit) {
    block()
    executePendingBindings()
}